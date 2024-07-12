<!-- 已测试接�? -->
<template>
  <div class="container">
    <div class="sidebar">
      <div class="sidebar-item" id="username">{{ username }}</div>
      <div class="sidebar-item" @click="goToStart">开�?</div>
      <div class="sidebar-item" @click="goToNotebook">笔记�?</div>
      <div class="sidebar-item community-button" @click="goToCommunity">
        <div class="icon-placeholder">
          <img src="/vue图片/图片3.png" alt="开始图�?" class="icon-image">
        </div>
        <span>发现社区</span>
      </div>
      <!-- <div class="sidebar-item" @click="goToTags">标签管理</div> -->
    </div>
    <div class="main-content">
      <div class="note-detail" v-if="note">
        <h1>{{ note.title }}</h1>
        <div class="note-info">
          <span>作�?: {{ note.username }}</span>
          <span>创建时间: {{ formatDate(note.createdAt) }}</span>
          <span>点赞�?: {{ likeCount }}</span>
          <button @click="toggleLike" :class="{ 'liked': isLiked }">
            {{ isLiked ? '取消点赞' : '点赞' }}
          </button>
        </div>
        <div class="note-content">
          {{ note.content }}
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
              <strong>{{ decodeUsername(comment.createdBy.username) }}:</strong>
              <p>{{ comment.content }}</p>
              <span>{{ formatDate(comment.createdAt) }}</span>
              
              <button v-if="canDeleteComment(comment)" @click="deleteComment(comment)">删除</button>
            </div>
          </div>
        </div>
      </div>
      <div v-else>加载�?...</div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      username: '',
      currentUser: null,
      note: null,
      comments: [],
      likeCount: 0,
      isLiked: false,
      newComment: '',
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
    //解码用户�?
    decodeUsername(username) {
      try {
        return decodeURIComponent(username);
      } catch (e) {
        console.error("Error decoding username:", e);
        return username; // 如果解码失败，返回原始用户名
      }
    },
    async fetchNoteDetail() {
      const noteId = this.$route.params.noteId;
      try {
        const response = await axios.get(`/api/notes/${noteId}`);
        if (response.data && response.data.code === 200) {
          this.note = response.data.data;
        } else {
          this.note = {
            noteId: noteId,
            username: "未知用户",
            title: "笔记未找�?",
            content: "抱歉，未找到对应的笔记内容�?",
            createdAt: new Date().toISOString(),
          };
        }
      } catch (error) {
        console.error("Error fetching note details:", error);
        this.note = {
          noteId: noteId,
          username: "未知用户",
          title: "笔记未找�?",
          content: "抱歉，未找到对应的笔记内容�?",
          createdAt: new Date().toISOString(),
        };
      }
      await this.fetchComments();
      await this.fetchLikeCount();
      await this.checkIfLiked();
    },
    async fetchCurrentUser() {
      try {
        const response = await axios.get('/api/user');
        if (response.data && response.data.code === 200) {
          this.currentUser = response.data.data;
          this.username = this.currentUser.username;
          console.log('用户名：',this.username);
        }
      } catch (error) {
        console.error("Error fetching current user:", error);
      }
    },
    async toggleLike() {
      const noteId = this.$route.params.noteId;
      try {
        const response = await axios.post(`/api/noteLikes/toggleLike`, { noteId });
        if (response.data && response.data.code === 200) {
          this.isLiked = response.data.data.isLikedByCurrentUser;
          this.likeCount = response.data.data.likes;
        }
      } catch (error) {
        console.error("Error toggling like:", error);
      }
    },
    async fetchLikeCount() {
      const noteId = this.$route.params.noteId;
      try {
        const response = await axios.get(`/api/noteLikes/count/${noteId}`);
        if (response.data && response.status === 200) {
          this.likeCount = response.data.count;
        }
      } catch (error) {
        console.error("Error fetching like count:", error);
      }
    },
    async checkIfLiked() {
      const noteId = this.$route.params.noteId;
      try {
        const response = await axios.get(`/api/noteLikes/isLiked/${noteId}`);
        if (response.data && response.status === 200) {
          this.isLiked = response.data.Liked;
        }
      } catch (error) {
        console.error("Error checking if liked:", error);
      }
    },
    async fetchComments() {
      const noteId = this.$route.params.noteId;
      try {
        const response = await axios.get(`/api/comments/view/${noteId}`);
        if (response.data && response.data.code === 200) {
          this.comments = response.data.data;
          console.log("Fetched comments:", this.comments);
          
          // 确保用户名一致�?
          const normalizeUsername = (username) => {
            try {
              return decodeURIComponent(username).trim().toLowerCase();
            } catch (e) {
              console.error("Error decoding username:", e);
              return username.trim().toLowerCase();
            }
          };
          
          if(this.currentUser) {
            this.currentUser.normalizedUsername = normalizeUsername(this.currentUser.username);
            this.currentUser.decodedUsername = decodeURIComponent(this.currentUser.username);
          }
          if(this.note) {
            this.note.normalizedUsername = normalizeUsername(this.note.username);
            this.note.decodedUsername = decodeURIComponent(this.note.username);
          }
          this.comments.forEach(comment => {
            if(comment.createdBy) {
              comment.createdBy.normalizedUsername = normalizeUsername(comment.createdBy.username);
              comment.createdBy.decodedUsername = decodeURIComponent(comment.createdBy.username);
            }
          });
        }
      } catch (error) {
        console.error("Error fetching comments:", error);
      }
    },
    async postComment() {
      if (!this.currentUser) {
        console.error("Current user not available");
        return;
      }
      const noteId = this.$route.params.noteId;
      try {
        const response = await axios.post(`/api/comments/post`, {
          noteId,
          content: this.newComment
        });
        if (response.data && response.data.code === 200) {
          await this.fetchComments();  // Refresh comments
          this.newComment = '';  // Clear textarea
        }
      } catch (error) {
        console.error("Error posting comment:", error);
      }
    },
    async deleteComment(comment) {
      if (!this.canDeleteComment(comment)) {
        console.error("No permission to delete this comment");
        return;
      }

      try {
        const response = await axios.delete(`/api/comments/delete/${this.note.noteId}/${comment.commentId}`, {
          headers: {
            Authorization: `Bearer-${encodeURIComponent(this.currentUser.username)}`
          }
        });

        if (response.data && response.data.code === 200) {
          console.log("Comment deleted successfully");
          // 从评论列表中移除已删除的评论
          const index = this.comments.findIndex(c => c.commentId === comment.commentId);
          if (index !== -1) {
            this.comments.splice(index, 1);
          }
        } else {
          console.error("Failed to delete comment:", response.data.message);
        }
      } catch (error) {
        console.error("Error deleting comment:", error);
      }
    },
    canDeleteComment(comment) {
      const currentUsername = this.currentUser ? this.currentUser.normalizedUsername : '';
      const noteUsername = this.note ? this.note.normalizedUsername : '';
      const commentUsername = comment.createdBy ? comment.createdBy.normalizedUsername : '';

      console.log("Note username:", this.note ? this.note.decodedUsername : '');
      console.log("Current username:", this.currentUser ? this.currentUser.decodedUsername : '');
      console.log("Comment created by username:", comment.createdBy ? comment.createdBy.decodedUsername : '');

      return noteUsername === currentUsername || commentUsername === currentUsername;
    },
    formatDate(dateString) {
      return new Date(dateString).toLocaleString();
    },
  },
  async mounted() {
    await this.fetchCurrentUser();
    await this.fetchNoteDetail();
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