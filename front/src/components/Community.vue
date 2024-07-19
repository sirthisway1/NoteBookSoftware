<!-- 已测试接口 -->
<template>
    <div class="container">
      <div class="sidebar">
        <div class="sidebar-user">
          <img :src="useravatar" alt="User Avatar" class="sidebar-avatar">
          <div class="sidebar-username" id="username">{{ username }}</div>
        </div>
        <div class="sidebar-item" @click="goToStart">开始</div>
        <div class="sidebar-item" @click="goToNotebook">笔记本</div>
        <div class="sidebar-item active" @click="goToCommunity">
          <el-icon :size="40"><Comment /></el-icon>
          发现社区
        </div>
        <div class="sidebar-item" @click="goToUserCenter">用户中心</div>
        <!-- <div class="sidebar-item" @click="goToTags">标签管理</div> -->
      </div>
      <div class="main-content">
        <div class="community">
          <h1>发现社区</h1>
        </div>
        <div class="note-container">
          <div class="note" v-for="note in filteredNotes" :key="note.noteId" @click="goToNoteDetail(note.noteId,note.username)">
            
            <div class="note-header">
              <img :src="note.avatar" alt="User Avatar" class="sidebar-avatar2">
              <div class="note-user"> {{ note.username }}</div>
              
              <!-- <div class="note-noieId">笔记id: {{ note.noteId }}</div> -->
              <div class="note-title">{{ note.title }}</div>
            </div>
            <div class="note-info">
              <div class="note-time">最后一次更新时间: {{ formatDate(note.updatedAt) }}</div>
              
            </div>
            <div class="note-content">{{ stripHTML(truncateContent(note.content))  }}</div>
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
      notes: [], // 存储所有公共笔记
    };
  },
  
  computed: {
    filteredNotes() {
      return this.notes.filter((note) => !note.isPrivate);
    },
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
    goToTags() {
      this.$router.push({ name: 'Tags' });
    },
    goToNoteDetail(noteId,username) {
      this.$router.push({ name: 'CommunityDetail', params: { noteId: noteId ,username: username} });
    },

    async fetchNotes() {

      const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          this.$router.push('/login');
          return;
        }

      try {
        const response = await axios.get('/api/notes/NoteShowWithUser',{
          headers: { token: token }
        });
        this.notes = response.data.data;
      } catch (error) {
        console.error("Error fetching notes:", error);
      }
    },
    truncateContent(content) {
      const maxLength = 400;
      if (content.length <= maxLength) {
        return content;
      }
      return content.substring(0, maxLength) + '...';
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
    stripHTML(value) {
      let doc = new DOMParser().parseFromString(value, 'text/html');
      return doc.body.textContent || "";
    }
  },
  async mounted() {
    await this.fetchNotes();
    await this.fetchCurrentUser();
  },
};
</script>


  
<style scoped>
.container {
  display: flex;
  height: 100vh;
  overflow: hidden;
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
  left: 40px;
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



.icon-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 确保图片填满圆形且不变形 */
}

.community-button:hover {
  background-color: #45a049;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #f5f7fa;
}

.community {
  padding: 30px;
  flex-shrink: 0;
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.community h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 20px;
}

.note-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.note {
  display: flex;
  flex-direction: column;
  margin-bottom: 30px;
  border: none;
  border-radius: 12px;
  width: 90%;
  height: auto;
  max-width: 800px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-left: auto;
  margin-right: auto;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  background-color: #ffffff;
}

.note:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.note-header {
  background-color: #87c6e5;
  padding: 15px;
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.sidebar-avatar2 {
  display: inline-block;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 90%;
  border: 2px solid #ffffff;
}

.note-user {
  display: inline-block;
  font-weight: bold;
  color: #ffffff;
  margin-right: 90%;
  
}

.note-title {
  font-family:'Lucida Sans', 'Lucida Sans Regular', 'Lucida Grande', 'Lucida Sans Unicode', Geneva, Verdana, sans-serif;
  font-size: 20px;
  font-weight: bold;
  color: #2e2e2e;
  margin-right: 90%;
}

.note-info {
  padding: 10px 20px;
  background-color: #f0f0f0;
  font-size: 0.9em;
  color: #666;
  border-bottom: 1px solid #e0e0e0;
}

.note-time {
  font-style: italic;
}

.note-content {
  background-color: #ffffff;
  padding: 20px;
  overflow: hidden;
  text-overflow: ellipsis;
  flex-grow: 1;
  color: #333;
  line-height: 1.6;
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .note {
    width: 95%;
  }

  .note-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .note-title {
    margin-left: 0;
    margin-top: 10px;
  }
}
</style>