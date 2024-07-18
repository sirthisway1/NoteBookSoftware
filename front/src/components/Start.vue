<!-- 已测试接口 -->
<template>
    <div class="container">
      <div class="sidebar">
        <div class="sidebar-user">
          <img :src="useravatar || 'default-avatar.png'" alt="User Avatar" class="sidebar-avatar">
          <div class="sidebar-username" id="username">{{ username }}</div>
        </div>
        <div class="sidebar-item active" @click="goToStart">
          <el-icon :size="40"><HomeFilled /></el-icon>
          开始
        </div>
        <div class="sidebar-item" @click="goToNotebook">笔记本</div>
        <div class="sidebar-item" @click="goToCommunity">发现社区</div>
        <div class="sidebar-item" @click="goToUserCenter">用户中心</div>
        <!-- <div class="sidebar-item" @click="goToCommunity">标签管理</div> -->
      </div>
      <div class="main-content">
        <div class="top-content">
          <el-col :span="12">
            <el-dropdown>
              <el-button type="primary">新建笔记<el-icon class="el-icon--right"><arrow-down /></el-icon></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="showCreateNoteModal(0)">新建日常笔记</el-dropdown-item>
                  <el-dropdown-item @click="showCreateNoteModal(1)">SWOT模板思维笔记</el-dropdown-item>
                  <el-dropdown-item @click="showCreateNoteModal(2)">5W1H模板思维笔记</el-dropdown-item>
                  <el-dropdown-item @click="showCreateNoteModal(3)">时间管理四象限模板思维笔记</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </el-col>
          <el-col :span="12" style="text-align: right;">
            <el-button @click="showNewNotebookModal" type="primary">新建笔记本</el-button>
          </el-col>
        </div>

        <div class="search-bar">
          <el-input
            v-model="searchInput"
            :placeholder="isTagMode === false ? '关键字搜索' : '标签搜索'"
            class="input-with-select"
          >
            <template #append>
              <el-button @click="searchTwo">搜索</el-button>
            </template>
          </el-input>
            <!-- <el-button @click="searchTwo">搜索</el-button> -->
            <el-switch
              v-model="isTagMode"
              active-text="标签搜索"
              inactive-text="关键字搜索"
              @change="toggleSearchMode"
            />
        </div>

        <div class="bottom-content">
          <el-row>
            <el-col :span="24" v-for="note in paginatedNotes" :key="note.noteId">
              <el-card class="note-item" shadow="hover">
                <template #header>
                  
                  <div class="card-header">
                    <el-icon :size="20" style="margin-top: 4px;"><Tickets /></el-icon>
                    <div class="title-and-date">
                      <h3>{{ note.title }}</h3>
                      <el-tag size="small" type="info" class="day">
                        最后修改: {{ formatDate(note.updatedAt) }}
                      </el-tag>
                    </div>
                    <div><el-button
                        :icon="Delete"
                        size="midle"
                        @click.stop="deleteNote(note.noteId)"
                        circle
                        class="delete-button"
                      >
                      <Delete style="width: 1em; height: 1em;" />
                    </el-button></div>
                    
                    
                  </div>
                </template>
                <div class="card-tags">
                  <el-tag
                    v-for="(tag, index) in note.tags"
                    :key="index"
                    size="small"
                    effect="light"
                    class="mr-5"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
              </el-card>
            </el-col>
          </el-row>

            <div class="pagination">
              <el-pagination
                layout="prev, pager, next"
                background
                :total="totalNotes"
                :page-size="notesPerPage"
                :current-page="currentPage"
                :color="white"
                @current-change="handlePageChange"
              >
              </el-pagination>
            </div>
        </div>
      </div>

    <div class="side-panel">
      <div class="side-button" @click="goToPrivate">查看私密列表</div>
      <div class="side-button" @click="goToWeekly">查看周报</div>
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
              <el-input
                type="text"
                v-model="newNotebookName"
                placeholder="请输入笔记本名称"
                clearable
              />
            </div>
            <div>
              <label>简介:</label>
              <textarea v-model="newNotebookDescription"></textarea>
            </div>
            <div class="modal-actions">
              <el-button type="primary" plain @click="createNotebook">创建</el-button>
            </div>
          </div>
        </div>
    </div>
    <!-- 包含模态框组件 -->
    <CreateNoteModal :visible="isCreateNoteModalVisible" :theNoteType="noteType" @close="hideCreateNoteModal"  @noteCreated="search" />
    </div>
</template>
  
<script>
import axios from 'axios';
import CreateNoteModal from './CreateNoteModal.vue';
import { ElMessage } from 'element-plus'
import { ref } from 'vue'
import { ElIcon } from 'element-plus';

export default {
  
  setup() {
    
    const isTagMode = ref(false)
    const searchMode = ref('keyword')

    const toggleSearchMode = (val) => {
      searchMode.value = val ? 'tag' : 'keyword'
      emit('update:searchMode', searchMode.value)
    }

    return {
      isTagMode,
      searchMode,
      toggleSearchMode
    }
  },
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
      notesPerPage: 3,
      
    };
  },
  computed: {
    totalNotes() {
      return this.filteredNotes.length;
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
    goToWeekly(){
      this.$router.push({ name: 'Week' });
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
          ElMessage({
            duration:1000,
            message: '请先登录',
            type: 'warning',
          })
          this.$router.push('/login');
          return;
        }

        let url = '/api/notes';
        const response = await axios.get(url+"/NoteShow", {
          headers: { token: token }
        });

        if (response.data.code === "200") {
          this.filteredNotes = response.data.data;
          this.currentPage = 1; // 重置到第一页
        } 
      } catch (error) {
        console.error('搜索笔记时出错:', error);
        if (error.response) {
          ElMessage({
            duration:1000,
            message: '搜索笔记时出错'+error.response.data.message,
            type: 'error',
          })
        }
      }
    },
    async searchTwo(){
      let url = '/api/notes';
      let params = {};

      const token = localStorage.getItem('token');
        if (!token) {
          ElMessage({
            duration:1000,
            message: '请先登录',
            type: 'warning',
          })
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
            this.currentPage = 1; // 重置到第一页
          } 
        })
        .catch(error => {
          console.error('请求出错:', error);
          if (error.response) {
            ElMessage({
            duration:1000,
            message: '搜索笔记时出错'+error.response.data.message,
            type: 'error',
          })
            this.search();
          }
          
        });
    },
    //更新搜索列表
    updateFilteredNotes() {
      const start = (this.currentPage - 1) * this.notesPerPage;
      const end = start + this.notesPerPage;
      this.filteredNotes = this.notes.slice(start, end);
    },
    handlePageChange(page) {
      this.currentPage = page;
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
          ElMessage({
            duration:1000,
            message: '创建成功',
            type: 'success',
          })
          this.hideNewNotebookModal();
        } 
      } catch (error) {
        console.error('创建笔记本时出错:', error);
        if (error.response) {
          ElMessage({
            duration:1000,
            message: '创建失败'+error.response.data.message,
            type: 'error',
          })
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

    // 删除笔记
    async deleteNote(noteId) {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('请先登录');
        return;
      }

      if (confirm("确定要删除这个笔记吗？")) {
        try {
          const response = await axios.delete(`/api/notes/delete/${noteId}`, {
            headers: { token: token }
          });
          
          if (response.data.code === "200") {
            ElMessage({
            duration:1000,
            message: '删除成功',
            type: 'success',
          })
            // 从列表中移除已删除的笔记
            this.notes = this.notes.filter(note => note.id !== noteId);
            await this.search();
            // 如果有分页，可能需要更新总页数
            // this.updateTotalPages();
          } else {
            alert(response.data.message || "笔记删除失败");
          }
        } catch (error) {
          console.error('删除笔记时发生错误:', error);
          alert("删除笔记失败，请稍后重试");
        }
      }
    },

    // // 更新总页数的方法（如果需要的话）
    // updateTotalPages() {
    //   // 根据笔记总数和每页显示数量计算总页数
    //   // 这里需要根据您的具体实现来调整
    //   this.totalPages = Math.ceil(this.notes.length / this.pageSize);
    // },

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
        this.updateFilteredNotes();
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
        this.updateFilteredNotes();
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
  color: #6e6e6e;
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

.sidebar-item span {
    font-size: 14px; /* 设置文字大小 */
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
  background-color: #1b79b3;
  color: white;
  padding: 10px 10px;
  width:100px;
  margin-top: 20px;
  cursor: pointer;
  border-radius: 5px;
  text-align: center;
  transition: background-color 0.3s;
}
.side-button:hover {
  background-color: rgb(106, 156, 236);
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
  padding: 5px;
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

.card-header {
  display: flex;
  justify-content: space-between; /* 在标题和按钮之间留出空间 */
}

.title-and-date{
  margin-right: 70%;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 5%;
}

</style>
  