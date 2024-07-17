<!-- 已测试完毕 -->
<template>
  <div class="container">
    <!-- 左侧侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-user">
          <img :src="useravatar || 'default-avatar.png'" alt="User Avatar" class="sidebar-avatar">
          <div class="sidebar-username" id="username">{{ username }}</div>
      </div>
      <div class="sidebar-item" @click="goToStart">开始</div>
      <div class="sidebar-item active" @click="goToNotebook">
        <el-icon :size="40"><Management /></el-icon>
       笔记本
      </div>
      <div class="sidebar-item" @click="goToCommunity">发现社区</div>
      <div class="sidebar-item" @click="goToUserCenter">用户中心</div>
      
    </div>
    
    <!-- 中间笔记本内容区 -->
    <!-- 中间笔记本内容区 -->
<div class="middle-section">
  <div class="notebook-header">
    <h2>{{ notebookName }}</h2>
    <div v-if="!isLoading" class="note-count-container">
      <p class="note-count">{{ notes?.length || 0 }}条笔记</p>
      <i class="fas fa-plus add-note" @click="showCreateNoteModal"></i>
    </div>
  </div>
  
  <button class="action-button" @click="backToStart">返回首页</button>
  <button class="action-button" @click="backToNotebook">目录</button>
  
  <div class="notebook-list">
    <div v-for="(note, index) in notes" :key="index" class="note-item" :class="{ selected: selectedNote && selectedNote.noteId === note.noteId }" @click="selectNote(note)">
      {{ note.title }}
    </div>
    <div v-if="hasMoreNotes" class="pagination">
      <button @click="prevPage">上一页</button>
      <button @click="nextPage">下一页</button>
    </div>
  </div>
</div>


    <!-- 右侧笔记详情区 -->
    <div class="right-section">
      <div class="note-summary">
        <h3>简介</h3>
        <div class="summary-content">{{ notebookSummary }}</div>
      </div>

      <div class="note-content">
        <div class="content-header">
          <h3>笔记内容</h3>
         
        </div>
        <div class="content-area">{{  }}</div>
      </div>

      <div class="keyward-summary">
        <h3>关键字显示</h3>
        <div class="keyword-content">{{  }}</div>
      </div>
    </div>

    <!-- 包含模态框组件 -->
    <CreateNoteModal :visible="isCreateNoteModalVisible" @close="hideCreateNoteModal" />
  </div>
</template>

<script>
import axios from 'axios';
import CreateNoteModal from './CreateNoteModal.vue';

export default {
  components: {
    CreateNoteModal,
  },
  data() {
    return {
      useravatar:'',
      notes: [],
      selectedNote: null,
      hasMoreNotes: false,
      searchQuery: '',
      notebookSummary: '',
      notebookName: '',
      isCreateNoteModalVisible: false,
      noteContent: '',
      isLoading: true,
      
    };
  },
  computed: {
    notebookId() {
      return this.$route.params.notebookId;
    }
  },
  methods: {
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
    goToNotebook() {
      this.$router.push({ name: 'Notebook' });
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
    //获取笔记本详细+笔记详细
    async fetchNotebookDetails() {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('请先登录');
        return;
      }
      
      this.isLoading = true;
      try {
        // 获取笔记本详情
        const notebookResponse = await axios.get(`/api/notebooks/${this.notebookId}`, {
          headers: { token: token }
        });
        
        if (notebookResponse.data.code === "200") {
          this.notebookName = notebookResponse.data.data.notebookDetail.name;
          this.notebookSummary = notebookResponse.data.data.notebookDetail.summary;
          
          // 笔记ID集合在 notebookResponse.data.data.noteIds 中
          const noteIds = notebookResponse.data.data.notebookDetail.noteId;
          
          // 获取每个笔记的详细信息
          const notePromises = noteIds.map(noteId => 
            axios.get(`/api/notes/detail/${noteId}`, {
              headers: { token: token }
            })
          );
          
          const noteResponses = await Promise.all(notePromises);
          
          // 处理每个笔记的响应
          this.notes = noteResponses.map(response => {
            if (response.data.code === "200") {
              return response.data.data;
            }
            return null;
          }).filter(note => note !== null);
        } else {
          console.error('获取笔记本详情失败:', notebookResponse.data.message);
        }
      } catch (error) {
        console.error('获取笔记本详情时出错:', error);
      } finally {
        this.isLoading = false;
      }
    },
    selectNote(note) {
      this.selectedNote = note;
      this.noteContent = note.content;
      this.$router.push({ name: 'NoteDetail', params: { notebookId: this.notebookId, noteId: note.noteId } });
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
  },
  mounted() {
    this.fetchNotebookDetails();
    this.fetchCurrentUser(); 
    
  }
};
</script>
  
<style>
  .container {
    display: flex;
    height: 100vh;
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

.sidebar-item.active{
  background-color: #409bf6;
  color: white;
}
/* 用户名以及头像 */
.sidebar-user {
  display: flex;
  align-items: center;
  padding: 15px;
  margin-bottom: 20px;
  position: relative; /* 添加这行 */
  left: 40px;
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

.icon-placeholder {
    width: 60px; /* 设置圆形宽度 */
    height: 60px; /* 设置圆形高度 */
    background-color: #87d37c; /* 设置圆形背景颜色 */
    border-radius: 50%; /* 使元素为圆形 */
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 10px; /* 圆形与文字之间的间距 */
}

.icon-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 确保图片填满圆形且不变形 */
}


.sidebar-item span {
    font-size: 14px; /* 设置文字大小 */
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
  /* 中间笔记本内容区样式 */
.middle-section {
  width: 300px; /* 调整宽度 */
  background-color: #f7f7f7; /* 更柔和的背景色 */
  padding: 20px; /* 增加内边距 */
  display: flex;
  flex-direction: column;
  border-right: 1px solid #e0e0e0;
  box-shadow: 2px 0 5px rgba(0,0,0,0.1); /* 添加阴影效果 */
}

.notebook-header {
  text-align: center;
  margin-bottom: 20px;
  font-family: 'Arial', sans-serif; /* 更改字体 */
}

.note-count-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1.1em; /* 调整字体大小 */
}

.note-count {
  flex: 1;
  text-align: center;
  color: #555; /* 更柔和的颜色 */
}

.add-note {
  cursor: pointer;
  font-size: 1.5em;
  color: #4CAF50; /* 改变颜色 */
}

.action-button {
  margin: 10px 0;
  padding: 10px 15px;
  background-color: #4CAF50; /* 绿色按钮 */
  border: none;
  border-radius: 5px; /* 添加圆角 */
  color: white; /* 按钮文字颜色 */
  text-align: center;
  cursor: pointer;
  font-size: 1em; /* 调整字体大小 */
  transition: background-color 0.3s ease; /* 添加过渡效果 */
}

.action-button:hover {
  background-color: #45a049; /* 按钮悬停颜色 */
}

.notebook-list {
  flex-grow: 1;
  overflow-y: auto;
  background-color: #f7f7f7; /* 白色背景 */
  border-radius: 5px; /* 添加圆角 */
  padding: 10px; /* 增加内边距 */
  box-shadow: 0 2px 5px rgba(0,0,0,0.1); /* 添加阴影效果 */
}

.note-item {
  padding: 15px;
  margin-bottom: 10px; /* 添加下间距 */
  background-color: #f7f7f7; /* 柔和的背景色 */
  border: 1px solid #e0e0e0; /* 边框颜色 */
  border-radius: 5px; /* 添加圆角 */
  cursor: pointer;
  transition: background-color 0.3s ease; /* 添加过渡效果 */
}

.note-item:hover {
  background-color: #e0f7fa; /* 悬停背景色 */
}

.note-item.selected {
  background-color: #77c1f2b2; /* 选中背景色 */
}

.pagination {
  display: flex;
  justify-content: space-between;
  margin-top: 10px; /* 添加上间距 */
}

.pagination button {
  background-color: #4CAF50; /* 绿色按钮 */
  color: white; /* 按钮文字颜色 */
  border: none;
  border-radius: 5px; /* 添加圆角 */
  padding: 5px 10px;
  cursor: pointer;
  transition: background-color 0.3s ease; /* 添加过渡效果 */
}

.pagination button:hover {
  background-color: #45a049; /* 按钮悬停颜色 */
}

  
  /* 右侧笔记详情区样式 */
  .right-section {
    flex-grow: 1;
    padding: 20px;
  }
  
  .note-summary{
    margin-bottom: 20px;
  }
  .note-content {
    bottom: 100%
  }
  
  .summary-content{
    border: 1px solid #d0d0d0;
    padding: 10px;
    height: 100px;
    background-color: #f9f9f9;
  }
  .content-area {
    border: 1px solid #d0d0d0;
    padding: 10px;
    height: 400px;
    background-color: #f9f9f9;
  }
  .keyword-content{
    border: 1px solid #d0d0d0;
    padding: 10px;
    height: 100px;
    background-color: #f9f9f9;
  }
</style>
  