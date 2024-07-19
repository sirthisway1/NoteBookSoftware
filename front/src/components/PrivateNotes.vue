<!-- 已测试接口 -->
<template>
  <div class="container">
    <div class="close-button" @click="goToStart">×</div>
    <div class="sidebar">
      <div class="sidebar-user">
          <img :src="useravatar" alt="User Avatar" class="sidebar-avatar">
          <div class="sidebar-username" id="username">{{ username }}</div>
        </div>
        <div class="sidebar-item active" @click="goToStart">
          <el-icon :size="40"><HomeFilled /></el-icon>
          开始
        </div>
      <div class="sidebar-item" @click="goToNotebook">笔记本</div>
      <div class="sidebar-item" @click="goToCommunity">发现社区</div>
      <div class="sidebar-item" @click="goToUserCenter">用户中心</div>
    </div>
    <div class="main-content">
      <div class="top-content">
        <h2>私密笔记列表</h2>
        <!-- <p>{{ privateNotes }}</p> -->
      </div>
      
      <div class="bottom-content">
        <div class="note-list">
          <div v-for="note in privateNotes" :key="note.noteId" class="note-item" @click="selectNote(note)">
            <h3>{{ note.title }}</h3>
            <p>标签: {{ note.tags.join(', ') }}</p>
            <p>最后修改时间: {{ formatDate(note.updatedAt) }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      useravatar:'',
      username: '',
      privateNotes: [],
    };
  },
  created() {
    this.fetchPrivateNotes();
  },
  methods: {
    async fetchPrivateNotes() {
      try {
        // 登录时存储了 token
        const token = localStorage.getItem('token');
        const response = await axios.get('/api/notes/NoteShow', {
          headers: {
            token: token
          }
        });
        if (response.data.code === "200") {
          this.privateNotes = response.data.data.filter(note => note.isPrivate);
        } 
      } catch (error) {
        console.error('获取笔记列表出错:', error);
      }
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
    selectNote(note) {
      this.selectedNote = note;
      this.$router.push({ 
        name: 'NoteDetail', 
        params: { 
          notebookId: note.notebookId, 
          noteId: note.noteId 
        } 
      });
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString('zh-CN', { 
        year: 'numeric', 
        month: '2-digit', 
        day: '2-digit', 
        hour: '2-digit', 
        minute: '2-digit' 
      }); // 自定义日期格式
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
    this.fetchCurrentUser(); 
  }
};
</script>

  <style scoped>
  .container {
    display: flex;
    height: 100vh;
  }
  
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
  left:40px;
}

.sidebar-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
  border: 4px solid #409bf6;
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
    width: 60px;
    height: 60px;
    background-color: #87d37c;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 10px;
  }

  .icon-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 确保图片填满圆形且不变形 */
}

  
  .sidebar-item span {
    font-size: 14px;
  }
  
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
    padding: 20px;
  }
  
  .top-content {
    margin-bottom: 20px;
  }
  
  .search-bar {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .search-bar input {
    flex-grow: 1;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
  }
  
  .search-button {
    width: 20px;
    height: 20px;
    background-color: #4c87af;
    border-radius: 50%;
    margin-left: 10px;
    cursor: pointer;
  }
  
  .note-list {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
  }
  
  .note-item {
    background-color: white;
    border-radius: 10px;
    padding: 20px;
    box-shadow: 5px 5px 5px rgba(0,0,0,0.1);
    transition: transform 0.3s, box-shadow 0.3s;
    width: calc(33.333% - 20px);
  }
  
  .note-item:hover {
    transform: translateY(-5px);
    box-shadow: 8px 8px 15px rgba(0,0,0,0.2);
  }
  
  .note-item h3 {
    margin: 0 0 10px 0;
    color: #4c7faf;
  }
  
  .note-item p {
    margin: 0;
    font-size: 0.9em;
    color: #666;
  }

  .close-button {
  position: absolute;
  top: 20px;
  right: 20px;
  font-size: 24px;
  cursor: pointer;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f0f0;
  border-radius: 50%;
  transition: background-color 0.3s;
}

.close-button:hover {
  background-color: #e0e0e0;
}
  </style>