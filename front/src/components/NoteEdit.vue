<!-- 已测试图片上传接口 -->
<template>
    <div class="container">
      <!-- 左侧侧边栏 -->
      <div class="sidebar">
        <div class="sidebar-item" id="username">{{ username }}</div>
        <div class="sidebar-item" @click="goToStart">开始</div>
        <div class="sidebar-item notebook-button">
        <div class="icon-placeholder"><img src="/vue图片/图片2.png" alt="开始图标" class="icon-image"></div>
        <span>笔记本</span>
      </div>
        <div class="sidebar-item" @click="goToCommunity">发现社区</div>
        <!-- <div class="sidebar-item" @click="goToCommunity">标签管理</div> -->
      </div>
      
       
      <!-- 右侧笔记编辑区 -->
    
      <div class="editor-section">
        
        <div class="editor-container ">

          
<div class="editor-header">
  <input type="text" v-model="noteTitle" placeholder="编辑标题" class="title-input"/>
  <button class="icon-button" @click="tagNote"><i class="fas fa-tags"></i></button>
  <!-- <button class="icon-button" @click="favoriteNote"><i class="fas fa-star"></i></button> -->
  <button class="icon-button" @click="privateNote"><i class="fas fa-lock"></i></button>
  <button class="icon-button" @click="closeEditor"><i class="fas fa-times"></i></button>
</div>


<div class="editor-body">
<template v-if="noteType === 0">
<!-- 富文本编辑器 -->
<Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" :mode="mode" />
<Editor
:defaultConfig="editorConfig"
:mode="mode"
v-model="valueHtml"
class="quill-editor"
@onCreated="handleCreated"
@onChange="handleChange"
@onDestroyed="handleDestroyed"
@onFocus="handleFocus"
@onBlur="handleBlur"
@customAlert="customAlert"
@customPaste="customPaste"
/>
</template>
<template v-else-if="noteType === 1">
<!-- 时间管理四象限分析 -->
<div class="quadrant">
<div class="quadrant-item">
<label>紧急且重要</label>
<textarea v-model="noteContent.ur_im"></textarea>
</div>
<div class="quadrant-item">
<label>不紧急但重要</label>
<textarea v-model="noteContent.nur_im"></textarea>
</div>
<div class="quadrant-item">
<label>紧急但不重要</label>
<textarea v-model="noteContent.ur_nim"></textarea>
</div>
<div class="quadrant-item">
<label>不紧急且不重要</label>
<textarea v-model="noteContent.nur_nim"></textarea>
</div>
</div>
</template>
<template v-else-if="noteType === 2">
<!-- SWOT分析 -->
<div class="swot">
<div class="swot-item">
<label>优势</label>
<textarea v-model="noteContent.advantage"></textarea>
</div>
<div class="swot-item">
<label>劣势</label>
<textarea v-model="noteContent.disadvantage"></textarea>
</div>
<div class="swot-item">
<label>机会</label>
<textarea v-model="noteContent.chance"></textarea>
</div>
<div class="swot-item">
<label>威胁</label>
<textarea v-model="noteContent.threat"></textarea>
</div>
</div>
</template>
<template v-else-if="noteType === 3">
<!-- 5W1H分析 -->
<div class="five-wh">
<div class="five-wh-item">
<label>What (什么)</label>
<textarea v-model="noteContent.what"></textarea>
</div>
<div class="five-wh-item">
<label>Why (为什么)</label>
<textarea v-model="noteContent.why"></textarea>
</div>
<div class="five-wh-item">
<label>Who (谁)</label>
<textarea v-model="noteContent.who"></textarea>
</div>
<div class="five-wh-item">
<label>Where (在哪里)</label>
<textarea v-model="noteContent.where"></textarea>
</div>
<div class="five-wh-item">
<label>When (何时)</label>
<textarea v-model="noteContent.when"></textarea>
</div>
<div class="five-wh-item">
<label>How (如何)</label>
<textarea v-model="noteContent.how"></textarea>
</div>
</div>
</template>
</div>
<div class="editor-footer">
  <button @click="saveNote" class="save-button">保存</button>
  <button @click="insertText">插入文本</button>
  <button @click="printHtml">打印 HTML</button>
  <button @click="disable">禁用</button>
</div>
</div>
      </div>
      <!-- 包含模态框组件 -->
      <CreateNoteModal :visible="isCreateNoteModalVisible" @close="hideCreateNoteModal" />
    </div>
    <TagModal 
    :visible="isTagModalVisible" 
    :tags="selectedNote ? selectedNote.tags : []"
    @close="closeTagModal"
    @update-tags="updateTags"
  />
</template>
  
  <script>

  import axios from 'axios';
  import TagModal from './TagModal.vue';

  import '@wangeditor/editor/dist/css/style.css';
  import { onBeforeUnmount, ref, shallowRef, onMounted } from 'vue';
  import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
  import CreateNoteModal from './CreateNoteModal.vue';
  
  export default {
  components: { Editor, Toolbar, CreateNoteModal,TagModal,
  },
  computed: {
  tagsForModal() {
    console.log('Tags for modal:', this.selectedNote ? this.selectedNote.tags : []);
    return this.selectedNote ? this.selectedNote.tags : [];
  },
  notebookId() {
    return this.$route.params.notebookId;
  },
  
  noteContent(){
    return this.$route.params.noteContent;
  }
},
  props: {
    id: Number,
    noteName: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      
      selectedNote: {
        noteId: this.$route.params.noteId,
        title: this.$route.params.noteName,
        content: this.getNoteContent(this.$route.params.noteContent),
        tags: this.$route.params.tags,
        type: parseInt(this.$route.params.noteType, 10),
        isPrivate: this.$route.params.isPrivate
      },
      hasMoreNotes: false,
      searchQuery: '',
      notebookSummary: '这里是简介',
      notebookName: '这里是笔记本名',
      isCreateNoteModalVisible: false,
      noteTitle: this.$route.params.noteName || '', // 设置默认值
      isTagModalVisible: false,
      // isFavorite: false,
      isPrivate: this.$route.params.isPrivate,
      noteType: parseInt(this.$route.params.noteType, 10),
      noteContent: this.getNoteContent(this.$route.params.noteContent),
      
      
      // 编辑器相关数据
      editorRef: shallowRef(null),
      valueHtml: this.$route.params.noteContent,
      toolbarConfig: {
        excludeKeys: ["insertVideo", "uploadVideo", "fullScreen"],
      },
      editorConfig: { 
        MENU_CONF: {
          uploadImage:{
            server: '/api/upload',
            fieldName: 'image', 
          }
      } },
    };
  },
  methods: {
    getNoteContent(content) {
    try {
      return JSON.parse(content);
    } catch (error) {
      return content;
    }
  },
  
    async fetchNoteDetails(noteId) {
  try {
    const token = localStorage.getItem('token'); // 假设令牌存储在 localStorage 中
        if (!token) {
          alert('用户未登录或会话已过期');
          return;
        }

        const response = await axios.get(`/api/notes/${noteId}`, {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });

    const data = await response.json();
    if (data.code === 200) {
      
      this.noteContent = data.data.content;
      this.isFavorite = data.data.isFavorite;
      this.isPrivate = data.data.isPrivate;
      this.noteTitle = data.data.title;
      this.valueHtml = data.data.content;
    } else if (data.code === 404) {
      alert('资源不存在');
    } else if (data.code === 501) {
      alert('Token 验证失败');
    } else if (data.code === 405) {
      alert('不合法的请求');
    } else {
      alert(`获取笔记详细信息失败: ${data.message}`);
    }
  } catch (error) {
    console.error(`Error fetching note details for ID ${noteId}:`, error);
  }
},
async mounted() {
  const notebookId = this.$route.params.notebookId;
  const noteId = this.$route.params.noteId;
  if (notebookId && noteId) {
    await this.fetchNoteDetails(noteId);
  }
},
tagNote() {
      if (this.selectedNote) {
        this.isTagModalVisible = true;
      } else {
        alert('无法识别当前笔记');
      }
    },

  privateNote() {
  this.isPrivate = !this.isPrivate; // 切换状态
  if (this.isPrivate) {
    alert("已将该笔记设为私密笔记");
  } else {
    alert("已将该笔记设为公开笔记");
  }
},
   // 新增方法用于关闭标签模态框
   closeTagModal() {
    this.isTagModalVisible = false;
  },

  updateTags(newTags) {
      if (this.selectedNote) {
        this.selectedNote.tags = newTags;
      }
    },
  // 新增方法用于添加标签
  async addTag(tag) {
    try {
      const response = await fetch(`/api/notes/${this.selectedNote.noteId}/tags`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ tag }),
      });

      const data = await response.json();
      if (data.code === 200) {
        this.selectedNote.tags.push(tag);
        alert('标签添加成功');
      } else {
        alert(`添加标签失败: ${data.message}`);
      }
    } catch (error) {
      console.error('Error adding tag:', error);
      alert('添加标签失败');
    }
  },
    prevPage() {
      // 实现上一页功能
    },
    nextPage() {
      // 实现下一页功能
    },
    showCreateNoteModal() {
      this.isCreateNoteModalVisible = true;
    },
    hideCreateNoteModal() {
      this.isCreateNoteModalVisible = false;
    },
    clearSearch() {
      this.searchQuery = '';
    },
    searchNotes() {
      // 实现搜索功能
    },
    goToStart() {
      this.$router.push({ name: 'Start' });
    },
    goToCommunity() {
      this.$router.push({ name: 'Community' });
    },
    backToStart() {
      this.$router.push({ name: 'Start' });
    },
    backToNotebook() {
      this.$router.push({ name: 'Notebook' });
    },
    async fetchNotebookDetails(notebookId) {
      try {
        const token = localStorage.getItem('token'); // 假设令牌存储在 localStorage 中
        if (!token) {
          alert('用户未登录或会话已过期');
          return;
        }

        const response = await axios.get(`/api/notebooks/${notebookId}`, {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });
        const data = response.data;
    if (data.code === 200) {
      this.selectedNote = data.data;
      this.notebookSummary = data.summary;
      this.notebookName = data.name;
      return data.data;
    } else {
      alert(`获取笔记本详细信息失败: ${data.message}`);
      return null;
    }
  } catch (error) {
    console.error(`Error fetching notebook details for ID ${notebookId}:`, error);
    return null;
  }
    },
    async mounted() {
      await this.fetchNotebookDetails();
    },
    handleCreated(editor) {
      this.editorRef = editor;
     
      editor.setHtml(this.valueHtml); // 初始化时设置HTML内容
      
    },
    handleChange(editor) {
      console.log('change:', editor.getHtml());
      this.valueHtml = editor.getHtml(); // 更新HTML内容
    },
    handleDestroyed(editor) {
      console.log('destroyed', editor);
    },
    handleFocus(editor) {
      console.log('focus', editor);
    },
    handleBlur(editor) {
      console.log('blur', editor);
    },
    customAlert(info, type) {
      alert(`【自定义提示】${type} - ${info}`);
    },
    customPaste(editor, event, callback) {
      console.log('ClipboardEvent 粘贴事件对象', event);
      editor.insertText('');
      callback(true);
    },
    insertText() {
      if (this.editorRef) {
        this.editorRef.insertText('hello world');
      }
    },
    printHtml() {
      if (this.editorRef) {
        console.log(this.editorRef.getHtml());
      }
    },
    disable() {
      if (this.editorRef) {
        this.editorRef.disable();
      }
    },

  
    // 更新笔记，PUT请求

    async saveNote() {
  try {
    const token = localStorage.getItem('token'); // 假设令牌存储在 localStorage 中
    if (!token) {
      alert('用户未登录或会话已过期');
      return;
    }

    // 根据笔记类型设置内容
    let noteContent = this.valueHtml; // 默认内容（用于 type 为 0 的笔记）
    if (this.noteType === 1 || this.noteType === 2 || this.noteType === 3) {
      noteContent = JSON.stringify(this.noteContent); // 对于思维笔记，转换为 JSON 字符串
    }

    const response = await axios.put(`/api/notes/${this.selectedNote.noteId}`, {
      title: this.noteTitle,
      content: noteContent,
      tags: this.selectedNote.tags,
      isPrivate: this.isPrivate,
    }, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });

    if (response.data.code === 200) {
      alert('笔记更新成功');
      // 更新 selectedNote 数据
      this.selectedNote.title = this.noteTitle;
      this.selectedNote.content = noteContent;
      this.selectedNote.isPrivate = this.isPrivate;
      this.$router.push({ name: 'NoteDetail', params: { notebookId: this.notebookId, noteId: this.selectedNote.noteId } });
    } else {
      alert(`更新笔记失败: ${response.data.message}`);
    }
  } catch (error) {
    console.error('Error updating note:', error);
    alert('更新笔记失败');
  }
},
    closeEditor() {
      this.$router.push({ name: 'NoteDetail' });
    },
  },
  async mounted() {
    if (this.$route.params.note) {
    this.selectedNote = JSON.parse(this.$route.params.note);
  }
    await this.fetchNotebookDetails();
  },
  watch: {
  '$route.params.noteId': {
    immediate: true,
    handler(newNoteId) {
      if (newNoteId) {
        this.fetchNoteDetails(newNoteId);
      }
    }
  }
},
};

  </script>
  


  <style scoped>



  .container {
    display: flex;
    height: 100vh;
    width: 100%;
  }
  
  /* 左侧侧边栏样式 */
  .sidebar {
  width: 200px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  border-right: 1px solid #e0e0e0;
  background-color: #ffffff;
  text-align: center;
  padding-top: 20px;
  box-shadow: 2px 0 5px rgba(0,0,0,0.1);
}

.sidebar-item {
  height: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px 0;
  cursor: pointer;
  color: #333;
  transition: background-color 0.3s;
}

.sidebar-item:hover {
  background-color: #f0f0f0;
}
  
.notebook-button {
  background-color: #4CAF50;
  color: white;
  border-radius: 20px;
  padding: 10px 20px;
  margin: 10px 20px;
  transition: background-color 0.3s;
}

.notebook-button:hover {
  background-color: #45a049;
}

  
  .note-count-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .note-count {
    flex: 1;
    text-align: center;
  }
  
  .add-note {
    cursor: pointer;
    font-size: 1.2em;
    color: #000000; /* 可根据需要修改颜色 */
  }
  
  .icon {
    width: 50px;
    height: 50px;
    background-color: #b0b0b0;
    margin-bottom: 20px;
  }
  
  .icon-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 确保图片填满圆形且不变形 */
}


  .sidebar-button {
    margin: 10px 0;
    padding: 10px;
    width: 100%;
    background-color: #d0d0d0;
    border: none;
    text-align: center;
    cursor: pointer;
  }
  
  .sidebar-button:hover {
    background-color: #c0c0c0;
  }
  
  /* 中间笔记本内容区样式 */
 .middle-section {
  width: 200px; /* 具体宽度 */
  background-color: #f0f0f0;
  padding: 10px;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #000000;
}
  
  .notebook-header {
    text-align: center;
    margin-bottom: 20px;
  }
  
  .search-container {
    position: relative;
    display: flex;
    align-items: center;
    border: 1px solid #ccc;
    padding: 5px;
    background-color: #fff;
  }
  
  .search-container input {
    border: none;
    outline: none;
    flex-grow: 1;
    padding-left: 25px; /* 为左侧图标留出空间 */
    padding-right: 2px; /* 为右侧图标留出空间 */
  }
  
  .search-icon {
    position: absolute;
    left: 10px;
    cursor: pointer;
  }
  
  .clear-icon {
    position: absolute;
    right: 10px;
    cursor: pointer;
  }
  
  .action-button {
    margin: 10px 0;
    padding: 10px;
    background-color: #d0d0d0;
    border: none;
    text-align: center;
    cursor: pointer;
  }
  
  .action-button:hover {
    background-color: #c0c0c0;
  }
  
  .notebook-list {
    flex-grow: 1;
    overflow-y: auto;
  }
  
  .note-item {
    padding: 30px;
    cursor: pointer;
  }
  
  .note-item.selected {
    background-color: #a0e0a0;
  }
  
  .pagination {
    display: flex;
    justify-content: space-between;
  }
  
    /* 右侧笔记编辑区样式 */
    .editor-section {
    flex-grow: 1; /* 确保右侧编辑区占据剩余空间 */
    padding: 20px;
    display: flex;
    flex-direction: column;
  }
  
  .editor-container {
    max-width: 800px;
    margin: 0 auto;
    flex-grow: 1;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    gap: 20px;
  }


 
  
  .editor-header {
    display: flex;
    align-items: center;
    gap: 10px;
    border-bottom: 1px solid #ccc;
    padding-bottom: 10px;
  }
  
  .title-input {
    flex-grow: 1;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 16px;
  }
  
  .icon-button {
    background: none;
    border: none;
    cursor: pointer;
    font-size: 18px;
  }
  
  .editor-body {
    flex-grow: 1; /* 让编辑器主体部分占据剩余空间 */
    overflow-y: auto; /* 防止内容过多时溢出 */
  }
  
  .editor-toolbar {
    border-bottom: 1px solid #ccc;
    margin-bottom: 10px;
  }
  
  .quill-editor {
    height: 400px;
  }
  
  .editor-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
  
  .save-button {
    background-color: #4CAF50;
    color: white;
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .save-button:hover {
    background-color: #45a049;
  }

  .tag-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.tag-modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 90%;
}
  </style>
  