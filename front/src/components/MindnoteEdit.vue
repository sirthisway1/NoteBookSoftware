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
      // 笔记相关数据
      notes: [
        {noteId: '1', title: '笔记1', type:0,
        content: JSON.stringify({
        "resId": 0,
        "parentId": null,
        "resName": "时间管理四象限分析",
        "extend": true,
        "children": [
          {"resId": 1, "parentId": 0, "resName": "第一象限：紧急且重要", "extend": true, "children": []},
          {"resId": 2, "parentId": 0, "resName": "第二象限：不紧急但重要", "extend": true, "children": []},
          {"resId": 3, "parentId": 0, "resName": "第三象限：紧急但不重要", "extend": true, "children": []},
          {"resId": 4, "parentId": 0, "resName": "第四象限：不紧急且不重要", "extend": true, "children": []}
        ]
      }),
      tags: ['工作', '会议']
      },

        {noteId: '2', title: '笔记2', type:1,content: '笔记2的内容' , tags: ['学习', '编程']},
        {noteId: '3', title: '笔记3', type:1,content: '笔记3的内容', tags: ['生活', '购物']},
        {noteId: '4', title: '笔记4', type:1,content: '笔记4的内容', tags: ['旅行', '摄影'] },
        {noteId: '5', title: '笔记5', type:1,content: '笔记5的内容', tags: ['美食', '烹饪']},
        {noteId: '6', title: '笔记6', type:1,content: '笔记6的内容', tags: ['健康', '运动']  },
      ],
      selectedNote: null,
      hasMoreNotes: false,
      searchQuery: '',
      notebookSummary: '这里是简介',
      notebookName: '这里是笔记本名',
      isCreateNoteModalVisible: false,
      noteTitle: this.$route.params.noteName || '', // 设置默认值
      isTagModalVisible: false,
      // isFavorite: false,
      isPrivate: false,
      
      
      // 编辑器相关数据
      editorRef: shallowRef(null),
      valueHtml: ref('<p>hello</p>'),
      toolbarConfig: {
        excludeKeys: ["insertVideo", "uploadVideo", "fullScreen"],
      },
      editorConfig: { MENU_CONF: {} },
    };
  },
  methods: {
    selectNote(note) {
      this.selectedNote = note;
      this.$router.push({ name: 'NoteEdit', params: { notebookId: this.notebookId, noteId: note.noteId } });
    },
    async fetchNoteDetails(noteId) {
  try {
    const response = await fetch(`/api/notes/${noteId}`, {
      method: 'GET',
    });

    const data = await response.json();
    if (data.code === 200) {
      this.noteContent = data.data.content;
      this.isFavorite = data.data.isFavorite;
      this.isPrivate = data.data.isPrivate;
      this.selectedNote = {
        ...this.selectedNote,
        tags: data.data.tags,
      };
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
  const noteId = this.$route.params.noteId;
  if (noteId) {
    this.isTagModalVisible = true;
  } else {
    alert('无法识别当前笔记');
  }
},
async favoriteNote() {
    try {
      const response = await fetch(`/api/notes/${this.selectedNote.noteId}/favorite`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      const data = await response.json();
      if (data.code === 200) {
        this.isFavorite = !this.isFavorite;
        alert(this.isFavorite ? '笔记已收藏' : '笔记已取消收藏');
        if (this.isFavorite) {
          this.favoriteNotes.push(this.selectedNote);
        } else {
          this.favoriteNotes = this.favoriteNotes.filter(note => note.noteId !== this.selectedNote.noteId);
        }
      } else {
        alert(`操作失败: ${data.message}`);
      }
    } catch (error) {
      console.error('Error toggling favorite:', error);
      alert('操作失败');
    }
  },
  async privateNote() {
    try {
      const response = await fetch(`/api/notes/${this.selectedNote.noteId}/private`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      const data = await response.json();
      if (data.code === 200) {
        this.isPrivate = !this.isPrivate;
        alert(this.isPrivate ? '笔记已设为私密' : '笔记已设为公开');
        if (this.isPrivate) {
          this.privateNotes.push(this.selectedNote);
        } else {
          this.privateNotes = this.privateNotes.filter(note => note.noteId !== this.selectedNote.noteId);
        }
      } else {
        alert(`操作失败: ${data.message}`);
      }
    } catch (error) {
      console.error('Error toggling private:', error);
      alert('操作失败');``
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
        const response = await fetch(`/api/notebooks/${notebookId}`, {
          method: 'GET',
        });
        const data = await response.json();
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
      console.log('created', editor);
      this.editorRef = editor;
    },
    handleChange(editor) {
      console.log('change:', editor.getHtml());
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

    //添加笔记，POST请求
    // 更新笔记，PUT请求
async saveNote() {
  if (!this.selectedNote) {
    alert('没有选中任何笔记');
    return;
  }

  const noteId = this.selectedNote.noteId; // 从选中的笔记中获取ID
  const title = this.noteTitle; // 从输入框获取标题
  const content = this.valueHtml; // 从编辑器获取内容
  const tags = this.selectedNote.tags || []; // 假设标签可以从编辑界面某处获取

  const payload = {
    title,
    content,
    tags
  };

  try {
    const response = await fetch(`/api/notes/${noteId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });

    const data = await response.json();

    switch (data.code) {
      case 200:
        alert('笔记更新成功');
        this.$router.push({ name: 'NoteDetail', params: { id: noteId } }); // 假设有一个笔记详情路由
        break;
      case 501:
        alert('token 验证失败');
        break;
      case 405:
        alert('不合法的请求');
        break;
      case 404:
        alert('笔记不存在');
        break;
      default:
        alert(`更新笔记失败: ${data.message}`);
    }
  } catch (error) {
    console.error('Error updating note:', error);
    alert('更新笔记时出现错误');
  }
},

    closeEditor() {
      this.$router.push({ name: 'NoteDetail' });
    },
  },
  async mounted() {
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
  