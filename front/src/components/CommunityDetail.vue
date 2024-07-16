<!-- 已测试接口 -->
<template>
  <div class="container">
    <div class="sidebar">
      <div class="sidebar-user">
          <img :src="useravatar || 'default-avatar.png'" alt="User Avatar" class="sidebar-avatar">
          <div class="sidebar-username" id="username">{{ username }}</div>
      </div>
      <div class="sidebar-item" @click="goToStart">开始</div>
      <div class="sidebar-item" @click="goToNotebook">笔记本</div>
      <div class="sidebar-item community-button" @click="goToCommunity">
        <div class="icon-placeholder">
          <img src="/vue图片/图片3.png" alt="开始图标" class="icon-image">
        </div>
        <span>发现社区</span>
      </div>
      <div class="sidebar-item" @click="goToUserCenter">用户中心</div>
      <!-- <div class="sidebar-item" @click="goToTags">标签管理</div> -->
    </div>
    <div class="main-content">
      <div class="note-detail" v-if="note">
        <h1>{{ note.title }}</h1>
        <img :src="writeravatar || 'default-avatar.png'" alt="User Avatar" class="sidebar-avatar">
        <div class="note-info">
          <span>作者: {{ note.username }}</span>
          <span>创建时间: {{ formatDate(note.createdAt) }}</span>
          <span>点赞数: {{ likeCount !== null ? likeCount : '加载中...' }}</span>
          <button @click="toggleLike" :class="{ 'liked': isLiked }">
            {{ isLiked ? '取消点赞' : '点赞' }}
          </button>
        </div>
        <div class="note-content">
          {{ stripHTML(note.content) }}
        </div>
        <!-- 评论部分 -->
        <div class="comments-section">
          <h3>评论</h3>
          <div class="add-comment">
            <textarea v-model="newComment" placeholder="添加评论..."></textarea>
            <button @click="postComment">发表评论</button>
          </div>
          <div class="comment-list">
            <div v-for="comment in comments" :key="comment.commentId" class="comment-item">
              <img :src="comment.avatar || 'default-avatar.png'" alt="User Avatar" class="sidebar-avatar">
              <strong>{{ comment.username }}:</strong>
              <p>{{ comment.content }}</p>
              <!-- <p>{{ comment }}</p> -->
              <span>{{ formatDate(comment.createdAt) }}</span>
              
              <button v-if="canDeleteComment(comment)" @click="deleteComment(comment)">删除</button>
            </div>
          </div>
        </div>
      </div>
      <div v-else>加载中...</div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      useravatar:'',
      currentname: '',
      currentUser: null,
      note: null,
      comments: [],
      likeCount: null,
      isLiked: false,
      newComment: '',
      writeravatar:'',
    };
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
   
    async fetchNoteDetail() {

      const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          this.$router.push('/login');
          return;
        }

      const noteId = this.$route.params.noteId;
      const textname = this.$route.params.username;
      
      try {
        const response = await axios.get(`/api/notes/detail/${noteId}`,{headers: { token: token }});
        if (response.data && response.data.code === "200") {
          this.note = response.data.data;
          this.note.username=textname;
          this.writeravatar=response.data.data.avatar;
        } 
      } catch (error) {
        console.error("Error fetching note details:", error);
        if (error.response) {
          alert('未搜索到: ' + error.response.data.message)};
        
      }

      await this.fetchComments();
      await this.fetchLikeCount();
      // await this.fetchLikeCount();
      await this.checkIfLiked();
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
    //切换点赞状态
    async toggleLike() {
      const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          this.$router.push('/login');
          return;
        }

      const noteId = this.$route.params.noteId;
      try {
        const response = await axios.post(`/api/noteLikes/toggleLike`, null,{params: {noteId:noteId} ,headers: { token: token }});
        if (response.data && response.data.code === "200") {
          await this.checkIfLiked();
          // this.isLiked = response.data.data.Liked;
          console.log("是否点赞:",this.isLiked);
          this.likeCount = response.data.data.likeCount;
          // await this.fetchLikeCount();
         
        }else{
          console.error("点赞错误:", response.data);
        }
      } catch (error) {
        console.error("Error toggling like:", error);
      }
    },
    //获取点赞数
    async fetchLikeCount() {
      const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          this.$router.push('/login');
          return;
        }

      const noteId = this.$route.params.noteId;
      try {
        const response = await axios.get(`/api/noteLikes/count/${noteId}`,{headers: { token: token }});
        if (response.data && response.data.code === "200") {
          console.log("Count response:", response);  // 添加这行
         this.likeCount=response.data.data.count;
         console.log("点赞数:", this.likeCount);
        }
        console.log("点赞获取成功");
      } catch (error) {
        console.error("Error fetching like count:", error);
      }
    },
    //判断是否点赞
    async checkIfLiked() {
      const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          this.$router.push('/login');
          return;
        }

      const noteId = this.$route.params.noteId;
      try {
        const response = await axios.get(`/api/noteLikes/isLiked/${noteId}`,{headers: { token: token }});
        if (response.data && response.data.code === "200") {
         
          this.isLiked = response.data.data.isLiked;
          console.log("点赞",this.isLiked);
          // console.log("点赞成功");
        }
      } catch (error) {
        console.error("Error checking if liked:", error);
      }
    },
    //获取评论
    async fetchComments() {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('请先登录');
        this.$router.push('/login');
        return;
      }

      const noteId = this.$route.params.noteId;
      try {
        const response = await axios.get(`/api/comments/view/${noteId}`,{headers: { 
            token: token
          }});
        if (response.data && response.data.code === "200") {
          // alert("查看评论成功")
          // 注意这里的数据结构变化
          this.comments = response.data.data;
          // this.postavatar = response.data.data.avatar;
          console.log("Fetched comments:", this.comments);
          
        } else {
          console.error("查看笔记错误:", response.data);
        }
      } catch (error) {
        console.error("Error fetching comments:", error);
        if (error.response) {
          alert('获得失败: ' + error.response.data.message);
        } 
      }
    },
    //发表评论
    async postComment() {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('请先登录');
        this.$router.push('/login');
        return;
      }

      if (!this.currentUser) {
        console.error("Current user not available");
        return;
      }

      const noteId = this.$route.params.noteId;
      const content = this.newComment;

      try {
        const response = await axios.post('/api/comments/post',null, {
          params: {
            noteId: noteId,
            content: content
          },
          headers: { 
            token: token
          }
        });
        console.log("发表");
        if (response.data && response.data.code === "200") {
          alert("发表成功");
          console.log("win");
          await this.fetchComments();  // Refresh comments
          this.newComment = '';  // Clear textarea
        }else{
          console.error("发表笔记错误:", response.data);
        }
      } catch (error) {
        console.error("Error posting comment:", error);
        if (error.response) {
          alert('发表失败: ' + error.response.data.message);
        } 
      }
    },
    //删除评论
    async deleteComment(comment) {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('请先登录');
        this.$router.push('/login');
        return;
      }


      if (!this.canDeleteComment(comment)) {
        console.error("No permission to delete this comment");
        return;
      }

      try {
        const response = await axios.delete(`/api/comments/delete/${this.note.noteId}/${comment.commentId}`, {
          headers: {
            token:token
          }
        });

        if (response.data && response.data.code === "200") {
          alert("评论删除成功");
          // 从评论列表中移除已删除的评论
          const index = this.comments.findIndex(c => c.commentId === comment.commentId);
          if (index !== -1) {
            this.comments.splice(index, 1);
          }
          await this.fetchComments();
        } else {
          alert("此贴已被不法分子付费拒绝删除")
          console.error("删除失败:", response.data.message);
        }
      } catch (error) {
        console.error("Error deleting comment:", error);
      }
    },
    canDeleteComment(comment) {
      const currentUsername = this.currentUser ? this.currentUser.username : '';
      const noteUsername = this.note ? this.note.username : '';
      const commentUsername = comment.username || '';

      console.log("Note username:", noteUsername);
      console.log("Current username:", currentUsername);
      console.log("Comment username:", commentUsername);

      return currentUsername === noteUsername || currentUsername === commentUsername;
    },
    formatDate(dateString) {
      return new Date(dateString).toLocaleString();
    },
    stripHTML(value) {
      let doc = new DOMParser().parseFromString(value, 'text/html');
      return doc.body.textContent || "";
    }
  },
  async mounted() {
    await this.fetchCurrentUser();
    await this.fetchNoteDetail();
    await this.fetchLikeCount();
    await this.checkIfLiked();
    // await this.fetchComments();
  },
};
</script>
  
<style scoped>
.container {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background-color: #f5f5f5;
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

/* 用户名以及头像 */
.sidebar-user {
  display: flex;
  align-items: center;
  padding: 15px;
  margin-bottom: 20px;
  position: relative; /* 添加这行 */
}

.sidebar-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
  flex-shrink: 0; /* 防止头像被压缩 */
  position: relative; /* 添加这行 */
  top: -10px; /* 调整这个值来控制上移的距离 */
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

.community-button {
  background-color: #4CAF50;
  color: white;
  border-radius: 20px;
  padding: 10px 20px;
  margin: 10px 20px;
  transition: background-color 0.3s;
}

.community-button:hover {
  background-color: #45a049;
}

.main-content {
  flex-grow: 1;
  padding: 30px;
  overflow-y: auto;
}

.note-detail {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.note-detail h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 20px;
}

.note-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  color: #666;
  font-size: 14px;
}

.note-content {
  line-height: 1.8;
  white-space: pre-wrap;
  color: #333;
  margin-bottom: 30px;
}

.icon-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 确保图片填满圆形且不变形 */
}

.like-button {
  background-color: #f0f0f0;
  border: none;
  padding: 8px 15px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.like-button:hover {
  background-color: #e0e0e0;
}

.liked {
  background-color: #4CAF50;
  color: white;
}

.comments-section {
  margin-top: 30px;
}

.comments-section h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 20px;
}

.add-comment {
  margin-bottom: 20px;
}

.add-comment textarea {
  width: 100%;
  height: 80px;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  resize: vertical;
}

.add-comment button {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
  transition: background-color 0.3s;
}

.add-comment button:hover {
  background-color: #45a049;
}

.comment-list {
  max-height: 400px;
  overflow-y: auto;
}

.comment-item {
  border-bottom: 1px solid #e0e0e0;
  padding: 15px 0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-item strong {
  color: #333;
}

.comment-item p {
  margin: 10px 0;
  color: #666;
}

.comment-item span {
  font-size: 12px;
  color: #999;
}

.comment-item button {
  background-color: #f44336;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.3s;
}

.comment-item button:hover {
  background-color: #d32f2f;
}
</style>