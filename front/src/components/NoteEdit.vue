<!-- 已测试图片上传接口 -->
<template>
  <div class="container">
    <!-- 左侧侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-user">
          <img :src="useravatar || 'default-avatar.png'" alt="User Avatar" class="sidebar-avatar">
          <div class="sidebar-username" id="username">{{ username }}</div>
      </div>
      <div class="sidebar-item" @click="goToStart">开始</div>
      <div class="sidebar-item notebook-button">
      <div class="icon-placeholder"><img src="/vue图片/图片2.png" alt="开始图标" class="icon-image"></div>
        <span>笔记本</span>
      </div>
      <div class="sidebar-item" @click="goToCommunity">发现社区</div>、
      <div class="sidebar-item" @click="goToUserCenter">用户中心</div>
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

<template v-if="noteType === 0">
  <!-- 录音的按钮 -->
  <button class="icon-button" @click="toggleRecording">
    <i v-if="!isRecording" class="fas fa-microphone"></i>
    <i v-else class="fas fa-music note-icon"></i>
  </button>
</template>
<button class="icon-button" @click="closeEditor"><i class="fas fa-times"></i></button>
</div>


<div class="editor-body">
<template v-if="noteType === 0">
  <div v-if="isEditorReady">
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
  </div>
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
  :selectedNote="selectedNote"
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
    useravatar:'',
    isEditorReady: false,
    selectedNote: {
      noteId: this.$route.params.noteId,
      title: this.$route.params.noteName,
      content: this.getNoteContent(this.$route.params.noteContent),
      tags: this.$route.params.tags || [],
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
    
    isRecording: false, // 录音状态
    mediaRecorder: null, // MediaRecorder实例
    audioChunks: [], // 存储录音数据块
    
    // 编辑器相关数据
    editorRef: shallowRef(null),
    valueHtml: '',
    toolbarConfig: {
      excludeKeys: ["insertVideo", "uploadVideo", "fullScreen"],
    },
    editorConfig: { 
      MENU_CONF: {
        uploadImage:{
          server: '/api/files/wang/upload',
          fieldName: 'file', 
          maxFileSize: 10 * 1024 * 1024,
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
 //获取笔记详细信息
  async fetchNoteDetails(noteId) {
    try {
      const token = localStorage.getItem('token'); // 令牌存储在 localStorage 中
        if (!token) {
          alert('用户未登录或会话已过期');
          return;
        }

        const response = await axios.get(`/api/notes/detail/${noteId}`, {headers: {token: token}});

      const data = await response.data;
      if (data.code === "200") {
        //更新
        this.selectedNote = {
        ...this.selectedNote,
        content: data.data.content,
        isPrivate: data.data.isPrivate,
        title: data.data.title
      };

        if(this.noteType === 1 || this.noteType === 2 || this.noteType === 3){
          this.noteContent = JSON.parse(data.data.content);
        }
        else{
        this.noteContent = data.data.content;
        }
        // this.noteContent = data.data.content;
        // this.isFavorite = data.data.isFavorite;
        this.isPrivate = data.data.isPrivate;
        this.noteTitle = data.data.title;
        this.valueHtml = data.data.content || '<p></p>';

        this.isEditorReady = true;
      }
    } catch (error) {
      console.error(`Error fetching note details for ID ${noteId}:`, error);
      if (error.response) {
            alert('未找到笔记详情: ' + error.response.data.message);
        }
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
  //设置私密
  async privateNote() {
    try {
      const token = localStorage.getItem('token'); // 假设令牌存储在 localStorage 中
      if (!token) {
        alert('用户未登录或会话已过期');
        return;
      }

      const newPrivacyStatus = !this.isPrivate; // 切换状态

      const response = await axios.put(`/api/notes/${this.selectedNote.noteId}/privacy`, null,{
        params:{
        isPrivate: newPrivacyStatus,
      },
        headers: {
          // 'Content-Type': 'application/json',
          token: token
        },
      });

      if (response.data.code === "200") {
        this.isPrivate = newPrivacyStatus; // 更新本地状态
        if (this.isPrivate) {
          alert("已将该笔记设为私密笔记");
        } else {
          alert("已将该笔记设为公开笔记");
        }
      } else {
        alert(`设置笔记私密失败: ${response.data.message}`);
      }
    } catch (error) {
      console.error('Error setting note privacy:', error);
      if (error.response) {
            alert('设置私密失败: ' + error.response.data.message);
        }
    }
  },


 // 新增方法用于关闭标签模态框
 closeTagModal() {
  this.isTagModalVisible = false;
},
//更新tag
updateTags(newTags) {
    if (this.selectedNote) {
      this.selectedNote.tags = newTags;
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
  goToUserCenter() {
      this.$router.push({ name: 'UserCenter' });
    },
  backToStart() {
    this.$router.push({ name: 'Start' });
  },
  backToNotebook() {
    this.$router.push({ name: 'Notebook' });
  },
  //获取笔记本详细
  async fetchNotebookDetails(notebookId) {
    try {
      const token = localStorage.getItem('token'); // 假设令牌存储在 localStorage 中
      if (!token) {
        alert('用户未登录或会话已过期');
        return;
      }

      const response = await axios.get(`/api/notebooks/${notebookId}`, {
        headers: {
          token:token
        },
      });
      const data = response.data;
  if (data.code === "200") {
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

      const response = await axios.put(`/api/notes/update/${this.selectedNote.noteId}`, {
        title: this.noteTitle,
        content: noteContent,
        tags: this.selectedNote.tags,
        isPrivate: this.isPrivate,
      }, {
        headers: {
          
          token: token
        },
      });

      if (response.data.code === "200") {
        alert('笔记更新成功');
        // 更新 selectedNote 数据
        this.selectedNote = {
        ...this.selectedNote,
        title: this.noteTitle,
        content: noteContent,
        isPrivate: this.isPrivate
      };
        this.$router.push({ name: 'NoteDetail', params: { notebookId: this.notebookId, noteId: this.selectedNote.noteId } });
        // await this.addTag();
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

  //获取当前用户
  async fetchCurrentUser() {
      const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          this.$router.push('/login');
          return;
        }

      try {
        const response = await axios.get('/api/user',{headers: { token: token }});
        if (response.data && response.data.code === "200") {
          this.currentUser = response.data.data;
          this.username = this.currentUser.username;
          this.useravatar = this.currentUser.avatar;
          console.log('用户名：',this.username);
        }
      } catch (error) {
        console.error("Error fetching current user:", error);
      }
    },
    //开始和结束录音
    toggleRecording() {
      if (this.isRecording) {
        this.stopRecording(); // 停止录音
      } else {
        this.startRecording(); // 开始录音
      }
    },
     // 开始录音
    async startRecording() {
      try {
        // 请求麦克风权限
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        // 创建 MediaRecorder 实例
        this.mediaRecorder = new MediaRecorder(stream);
        // 当有音频数据可用时，将其存储到 audioChunks 中
        this.mediaRecorder.ondataavailable = event => {
          this.audioChunks.push(event.data);
        };
        // 当录音停止时，处理录音数据
        this.mediaRecorder.onstop = this.handleRecordingStop;
        // 开始录音
        this.mediaRecorder.start();
        // 设置录音状态为 true
        this.isRecording = true;
      } catch (error) {
        console.error('Error accessing microphone:', error); // 处理获取麦克风权限错误
      }
    },
    // 停止录音
    stopRecording() {
      if (this.mediaRecorder) {
        this.mediaRecorder.stop(); // 停止录音
        this.isRecording = false; // 设置录音状态为 false
      }
    },
    // 处理录音停止事件
    async handleRecordingStop() {
      // 将录音数据块合并成一个 Blob
      const audioBlob = new Blob(this.audioChunks, { type: 'audio/wav' });
      
      // 创建一个下载链接
      const downloadLink = document.createElement('a');
      downloadLink.href = URL.createObjectURL(audioBlob);
      downloadLink.download = `recording_${new Date().getTime()}.wav`; // 设置下载文件名
      
      // 将链接添加到文档中并模拟点击
      document.body.appendChild(downloadLink);
      downloadLink.click();
      
      // 清理
      document.body.removeChild(downloadLink);
      URL.revokeObjectURL(downloadLink.href);
      //重置
      this.audioChunks = [];

      const formData = new FormData();
      formData.append('file', audioBlob, 'audio.wav');

      try {
        const response = await axios.post('/api/model/uploadAudio', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
        // 从服务器响应中获取转换后的文本内容
        const text = response.data.data.text;
        // 将文本内容插入到编辑器中
        this.valueHtml += `<p>${text}</p>`;
      } catch (error) {
        console.error('Error uploading audio:', error); // 处理上传错误
      }
    },
  },
async mounted() {
  await this.fetchCurrentUser();
  if (this.$route.params.note) {
  this.selectedNote = JSON.parse(this.$route.params.note);
}
  await this.fetchNotebookDetails();
  //更新
  if (this.$route.params.noteId) {
    await this.fetchNoteDetails(this.$route.params.noteId);
  }
  this.$nextTick(() => {
    if (this.editorRef) {
      this.editorRef.setHtml(this.valueHtml);
    }
  });
},
watch: {
  '$route.params.noteId': {
    immediate: true,
    handler(newNoteId) {
      if (newNoteId) {
        this.fetchNoteDetails(newNoteId);
      }
    }
  },
  valueHtml: {
    immediate: true,
    handler(newValue) {
      this.$nextTick(() => {
        if (this.editorRef) {
          this.editorRef.setHtml(newValue);
        }
      });
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

/* 用户名以及头像 */
.sidebar-user {
  display: flex;
  align-items: center;
  padding: 15px;
  margin-bottom: 20px;
  position: relative; /* 添加这行 */
  left:40px
}

.sidebar-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
  flex-shrink: 0; /* 防止头像被压缩 */
  position: relative; /* 添加这行 */
  top: 0px; /* 调整这个值来控制上移的距离 */
}

.sidebar-username {
  font-weight: bold;
  flex-grow: 1;
  text-align: left;
  display: flex;
  align-items: center; /* 垂直居中文本 */
  min-height: 40px; /* 与头像高度一致 */
}
/* 用户名以及头像 */

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

.note-icon {
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
