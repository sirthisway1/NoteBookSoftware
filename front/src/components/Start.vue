<!-- 已测试接口 -->
<template>
    <div class="container">
      <div class="sidebar">
        <div class="sidebar-user">
          <img :src="useravatar || 'default-avatar.png'" alt="User Avatar" class="sidebar-avatar">
          <div class="sidebar-username" id="username">{{ username }}</div>
        </div>
        <div class="sidebar-item start-button" @click="goToStart">
          <div class="icon-placeholder"><img src="/vue图片/图片1.png" alt="开始图标" class="icon-image">
          </div>
          <span>开始</span>
        </div>
        <div class="sidebar-item" @click="goToNotebook">笔记本</div>
        <div class="sidebar-item" @click="goToCommunity">发现社区</div>
        <div class="sidebar-item" @click="goToUserCenter">用户中心</div>
        <!-- <div class="sidebar-item" @click="goToCommunity">标签管理</div> -->
      </div>
      <div class="main-content">
        <div class="top-content">
          <div class="top-left">
            <div class="button-container">
              <div class="button">新建笔记</div>
              <div class="dropdown">
                <div class="dropdown-item" @click="showCreateNoteModal(0)">新建日常笔记</div>
                <div class="dropdown-item" @click="showCreateNoteModal(1)">时间管理四象限模板思维笔记</div>
                <div class="dropdown-item" @click="showCreateNoteModal(2)">SWOT模板思维笔记</div>
                <div class="dropdown-item" @click="showCreateNoteModal(3)">5W1H模板思维笔记</div>
               
              </div>
            </div>
          </div>
          <div class="top-right">
            <div class="button" @click="showNewNotebookModal">新建笔记本</div>
          </div>
        </div>
        <div class="search-bar">
          <input type="text" v-model="searchInput" :placeholder="searchMode === 'keyword' ? '关键字搜索' : '标签搜索'">
          
          <button @click="search" class="search-button">搜索全部笔记</button>
          <button @click="searchTwo" class="search-button">搜索</button>
          <div class="toggle-button" @click="toggleSearchMode">
            {{ searchMode === 'keyword' ? '切换到标签搜索' : '切换到关键字搜索' }}
          </div>
        </div>
        <div class="bottom-content">
          <div class="note-list">
            <div v-for="note in filteredNotes" :key="note.noteId" class="note-item">
              <h3>{{ note.title }}</h3>
              <p>最后修改日期: {{ formatDate(note.updatedAt) }}</p>
              <!-- <p>创建日期: {{ formatDate(note.createdAt) }}</p> -->
              <p>标签：
                <span v-for="(tag, index) in note.tags" :key="index" class="tag">
                  {{ tag }}{{ index < note.tags.length - 1 ? ', ' : '' }}
                </span>
              </p>
            </div>
          </div>
          <div class="pagination">
            <button @click="prevPage" :disabled="currentPage === 1">上一页</button>
            <span>{{ currentPage }} / {{ totalPages }}</span>
          <button @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
        </div>
        </div>
      </div>

    <div class="side-panel">
      <div class="side-button" @click="goToPrivate">查看私密列表</div>
    </div>
    <!-- 新建笔记本的模态框 -->
    <div class="modal" v-if="isModalVisible">
        <div class="modal-content">
          <div class="modal-header">
            <h2>新建笔记本</h2>
            <span class="close" @click="hideNewNotebookModal">&times;</span>
          </div>
          <div class="modal-body">
            <h3>基本信息</h3>
            <div>
              <label>笔记本名称:</label>
              <input type="text" v-model="newNotebookName">
            </div>
            <div>
              <label>简介:</label>
              <textarea v-model="newNotebookDescription"></textarea>
            </div>
            <div class="modal-actions">
              <button @click="createNotebook">创建</button>
            </div>
          </div>
        </div>
    </div>
    <!-- 包含模态框组件 -->
    <CreateNoteModal :visible="isCreateNoteModalVisible" :theNoteType="noteType" @close="hideCreateNoteModal" />
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
      username: '',
      isModalVisible: false,
      isCreateNoteModalVisible: false,
      newNotebookName: '',
      newNotebookDescription: '',
      notes: [],
      searchInput: '',
      searchMode: 'keyword',
      filteredNotes: [],
      currentPage: 1,
      notesPerPage: 6,
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.filteredNotes.length / this.notesPerPage);
    },
    paginatedNotes() {
      const start = (this.currentPage - 1) * this.notesPerPage;
      const end = start + this.notesPerPage;
      return this.filteredNotes.slice(start, end);
    }
  },
  methods: {
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
    goToPrivate() {
      this.$router.push({ name: 'PrivateNotes' });
    },
    toggleSearchMode() {
      this.searchMode = this.searchMode === 'keyword' ? 'tag' : 'keyword';
      this.searchInput = '';
      this.search();
    },
    async search() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          this.$router.push('/login');
          return;
        }

        let url = '/api/notes';
        const response = await axios.get(url+"/NoteShow", {
          headers: { token: token }
        });

        if (response.data.code === "200") {
          this.filteredNotes = response.data.data;
        } 
      } catch (error) {
        console.error('搜索笔记时出错:', error);
        if (error.response) {
            alert('未搜索到全部笔记: ' + error.response.data.message);
        }
      }
    },
    async searchTwo(){
      let url = '/api/notes';
      let params = {};

      const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          this.$router.push('/login');
          return;
        }

      if (this.searchInput.trim() !== '') {
        if (this.searchMode === 'keyword') {
          url += '/searchByKeyword';
          params.keyword = this.searchInput.trim();
        } else {
          url += '/searchByTags';
          const tags = this.searchInput.split(',').map(tag => tag.trim()).filter(tag => tag);
          params.tags = tags.join(',');
        }
      }

      // 使用 axios 发送请求
      axios.get(url, { params: params,headers: { token: token }})
        .then(response => {
          if (response.data.code === "200") {
            // 处理成功响应
            this.filteredNotes = response.data.data;
          } 
        })
        .catch(error => {
          console.error('请求出错:', error);
          if (error.response) {
            alert('未搜索到: ' + error.response.data.message);
        }
        });
    },
    //传入type参数
    showCreateNoteModal(type) {
      this.isCreateNoteModalVisible = true;
      this.noteType = type;
    },
    hideCreateNoteModal() {
      this.isCreateNoteModalVisible = false;
    },
    showNewNotebookModal() {
      this.isModalVisible = true;
    },
    hideNewNotebookModal() {
      this.isModalVisible = false;
    },
    async createNotebook() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          return;
        }

        const response = await axios.post('/api/notebooks/createNotebooks', {
          name: this.newNotebookName,
          summary: this.newNotebookDescription,
        }, {
          headers: { token: token }
        });

        if (response.data.code === "200") {
          alert('笔记本创建成功');
          this.hideNewNotebookModal();
        } 
      } catch (error) {
        console.error('创建笔记本时出错:', error);
        if (error.response) {
            alert('笔记本创建失败: ' + error.response.data.message);
        }
      }

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
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString('zh-CN', { 
        year: 'numeric', 
        month: '2-digit', 
        day: '2-digit', 
        hour: '2-digit', 
        minute: '2-digit' 
      });
    },

    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },
  },
  mounted() {
    this.fetchCurrentUser();
    this.search(); // 组件挂载时获取笔记列表
    
  }
};
</script>
  
<style scoped>
  .container {
  display: flex;
  height: 100vh;
  width: 100%;
  position: fixed;
  top: 0;
  left: 0;
}
  
.sidebar {
  width: 200px;
  height: 100%;
  position: fixed;
  top: 0;
  left: 0;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  border-right: 1px solid #e0e0e0;
  background-color: #ffffff;
  text-align: center;
  padding-top: 20px;
  box-shadow: 2px 0 5px rgba(0,0,0,0.1);
  z-index: 10;
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

/* 选中效果 */
.start-button {
  background-color: #4CAF50;
  color: white;
  border-radius: 20px;
  padding: 10px 20px;
  margin: 10px 20px;
  transition: background-color 0.3s;
}

.start-button:hover {
  background-color: #45a049;
}



.main-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  margin-left: 200px;
  padding: 20px;
    background-color: #f5f7fa;
  overflow-y: auto;
}
  
.top-content {
    height: 7%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px;
  }
  
  .top-left, .top-right {
    position: relative; /* 相对定位 */
    background-color: #a2ff93;
  }
  .button-container {
    position: relative; /* 相对定位 */
    background-color: #a2ff93;
  }
  
  .button {
    background-color: #a2ff93;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 150px;
    height: 50px;
    color: rgb(0, 0, 0);
    border-radius: 25px;
    cursor: pointer;
    transition: var(--transition);
  }
  
  
 
  .dropdown {
    display: none; /* 默认隐藏 */
    position: absolute; /* 绝对定位 */
    top: 100%; /* 紧挨着按钮下面 */
    left: 0;
    width: 100%; /* 与按钮同宽 */
    background-color: #4bffbd;
    border-radius: 5px;
    flex-direction: column;
    z-index: 1; /* 确保下拉框在前面 */
    
  }
  
  .dropdown-item {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 150px; /* 与按钮同宽 */
    height: 50px; /* 与按钮同高 */
    border-top: 1px solid #ffffff;
    cursor: pointer;
  }
  
  
  .button-container:hover .dropdown {
    background-color: #dcfff2;
    display: flex; /* 鼠标悬停时显示下拉框 */
  }
  
  /* 搜索框样式 */
  .search-bar {
    height: 50px;
    display: flex;
    align-items: center;
    padding: 0 20px;
    position: relative;
    top:-10px
  }
  
  .search-bar input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    margin-top: 0px;
    top:-10px
  }
  
  .search-button {
  margin-left: 10px;
  padding: 0px 15px; /* 调整内边距使按钮变得扁平 */
  background-color: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 8px; /* 减少边框半径 */
  transition: background-color 0.3s;
}

.search-button:hover {
  background-color: #45a049;
}

.toggle-button {
  margin-left: 10px;
  padding: 0px 15px; /* 调整内边距使按钮变得扁平 */
  background-color: #9affb8;
  border: 1px solid #9affb8;
  border-radius: 8px; /* 减少边框半径 */
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 14px; /* 调整字体大小 */
}

.toggle-button:hover {
  background-color: #4bffae;
}

  /* 搜索框样式 */

.bottom-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  background-color: #f9f9f9;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 0 15px rgba(0,0,0,0.1);
}
  
.note-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}
  
.side-panel {
  width: 200px;
  padding: 20px;
  background-color: #f0f0f0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

  .side-buttons {
    width: 150px; /* 调整按钮宽度 */
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    padding-right: 20px;
    padding-top: 200px; /* 增加左边间隔 */
  }
  
  .side-button {
  background-color: #8b4513;
  color: white;
  padding: 15px 20px;
  margin-top: 20px;
  cursor: pointer;
  border-radius: 5px;
  text-align: center;
  transition: background-color 0.3s;
}
.side-button:hover {
  background-color: #a0522d;
}
  .modal {
    display: flex;
    position: fixed;
    z-index: 1;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    overflow: auto;
    background-color: rgba(0,0,0,0.4);
    justify-content: center;
    align-items: center;
  }
  
  /* 新建笔记本UI*/
  .modal-content {
    background-color: #fefefe;
    padding: 20px;
    border: 1px solid #888;
    width: 40%;
    border-radius: 10px;
  }
  
  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 25%;
  }
  
  .modal-header h2 {
    margin: 0;
    flex: 1;
  }
  
  
  .modal-header .close {
    color: #aaa;
    font-size: 28px;
    font-weight: bold;
    cursor: pointer;
  }
  
  .modal-header .close:hover,
  .modal-header .close:focus {
    color: black;
    text-decoration: none;
    cursor: pointer;
  }
  
  .modal-body {
    margin-top: 10px;
  }
  
  .modal-body h3 {
    margin: 10px 0;
  }
  
  .modal-body div {
    margin-bottom: 10px;
  }
  
  .modal-body label {
    display: block;
    margin-bottom: 5px;
  }
  
  .modal-body input, .modal-body textarea {
    width: 100%;
    padding: 8px;
    box-sizing: border-box;
  }
  
  .modal-body textarea {
    height: 100px; /* 设置简介文本框的高度 */
    resize: none;
  }
  
  .modal-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 10px;
  }
  
  .modal-actions button {
    padding: 10px 20px;
    font-size: 1rem;
    cursor: pointer;
  }

  .note-item {
  background-color: white;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 5px 5px 5px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
}

.note-item:hover {
  transform: translateY(-5px);
  box-shadow: 8px 8px 8px rgba(0,0,0,0.2);
}

.note-item h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 1.2em;
}

.note-item p {
  margin: 5px 0;
  font-size: 0.9em;
  color: #666;
}

.tag {
  display: inline-block;
  background-color: #e0e0e0;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.8em;
  margin-right: 5px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.pagination button {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 10px 15px;
  margin: 0 10px;
  cursor: pointer;
  border-radius: 5px;
  transition: background-color 0.3s;
}

.pagination button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.pagination button:hover:not(:disabled) {
  background-color: #45a049;
}

</style>
  