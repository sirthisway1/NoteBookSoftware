<!-- 已测试完毕 -->
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
    
    <!-- 中间笔记本内容区 -->
    <div class="middle-section">
      <div class="notebook-header">
        <h2>{{ notebookName }}</h2>
        <div v-if="!isLoading" class="note-count-container">
          <p class="note-count">{{ notes?.length || 0 }}条笔记</p>
          <i class="fas fa-plus add-note" @click="showCreateNoteModal"></i>
        </div>

        <div class="search-container">
          <i class="fas fa-search search-icon" @click="searchNotes"></i>
          <input type="text" v-model="searchQuery" />
          <i class="fas fa-times clear-icon" @click="clearSearch"></i>
        </div>
      </div>

      <button class="action-button" @click="backToStart">返回首页</button>
      <button class="action-button" @click="backToNotebook">目录</button>
      <div class="notebook-list">
        <div v-for="(note, index) in notes" :key="index" class="note-item" :class="{ selected: selectedNote && selectedNote.noteId === note.noteId }" @click="selectNote(note)">
          {{ note.title }}
        </div>
        <!-- 若有更多笔记，添加翻页功能 -->
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
    async fetchNotebookDetails() {
      this.isLoading = true;
      try {
        const response = await axios.get(`/api/notebooks/${this.notebookId}`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        });
        
        if (response.data.code === 200) {
          this.notebookName = response.data.data.name;
          this.notebookSummary = response.data.data.summary;
          this.notes = response.data.data.notes;
        } else {
          console.error('获取笔记本详情失败:', response.data.message);
        }
        this.isLoading = false;
      } catch (error) {
        console.error('获取笔记本详情时出错:', error);
        this.isLoading = false;
      }
    },
    selectNote(note) {
      this.selectedNote = note;
      this.noteContent = note.content;
      this.$router.push({ name: 'NoteDetail', params: { notebookId: this.notebookId, noteId: note.noteId } });
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
    prevPage() {
      // 实现上一页功能
    },
    nextPage() {
      // 实现下一页功能
    }
  },
  mounted() {
    this.fetchNotebookDetails();
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
    width: 200px;
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
    height: 420px;
    background-color: #f9f9f9;
  }
</style>
  