<template>
    <div class="container">
      <div class="sidebar">
        <div class="sidebar-item" id="username">{{ username }}</div>
        <div class="sidebar-item" @click="goToStart">开始</div>
        <div class="sidebar-item" @click="goToNotebook">笔记本</div>
        <div class="sidebar-item community-button" @click="goToCommunity">
          <div class="icon-placeholder"><img src="/vue图片/图片3.png" alt="开始图标" class="icon-image"></div>
          <span>发现社区</span>
        </div>
        <!-- <div class="sidebar-item" @click="goToTags">标签管理</div> -->
      </div>
      <div class="main-content">
        <div class="note-detail" v-if="note">
          <h1>{{ note.title }}</h1>
          <div class="note-info">
            <span>作者: {{ note.username }}</span>
            <span>创建时间: {{ formatDate(note.createdAt) }}</span>
            <span>点赞数: {{ likeCount }}</span>
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
              <strong>{{ comment.createdBy.username }}:</strong>
              <p>{{ comment.content }}</p>
              <span>{{ formatDate(comment.createdAt) }}</span>
              <button v-if="canDeleteComment(comment)" @click="deleteComment(comment.commentId)">删除</button>
            </div>
          </div>
        </div>
        </div>
        <div v-else>加载中...</div>

      </div>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        username: '这里填写用户名',
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
      goToTags() {
        // 如果有标签管理页面，需要添加对应的路由
        this.$router.push({ name: 'Tags' });
      },
      async fetchNoteDetail() {
        const noteId = this.$route.params.noteId;

        // 模拟API延迟
        await new Promise(resolve => setTimeout(resolve, 500));

        // 模拟从Community.vue中获取的笔记数据
        const mockNotes = [
            {
            noteId: "123456",
            username: "用户1",
            title: "My Note 1",
            content: "你好锕 Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here.Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here.Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here.Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here. Note content 1 here.",
            tags: ["tag1", "tag2"],
            type: 2,
            isPrivate: false,
            createdAt: "2023-04-01T12:00:00Z",
            updatedAt: "2023-04-01T12:00:00Z",
            },
            {
            noteId: "654321",
            username: "用户2",
            title: "My Note 2",
            content: "你好 content 2 here. Note content 2 here. Note content 2 here. Note content 2 here. Note content 2 here. Note content 2 here. Note content 2 here. Note content 2 here. Note content 2 here.",
            tags: ["tag3"],
            type: 2,
            isPrivate: false,
            createdAt: "2023-05-01T12:00:00Z",
            updatedAt: "2023-05-01T12:00:00Z",
            },
            // ... 其他笔记数据 ...
            
        ];

        // 查找对应noteId的笔记
        const note = mockNotes.find(note => note.noteId === noteId);

        if (note) {
            this.note = note;
        } else {
            // 如果没有找到对应的笔记，设置一个默认值或显示错误信息
            this.note = {
            noteId: noteId,
            username: "未知用户",
            title: "笔记未找到",
            content: "抱歉，未找到对应的笔记内容。",
            createdAt: new Date().toISOString(),
            };
        }
        await this.fetchComments();
        await this.fetchLikeCount();
        await this.checkIfLiked();
    },

    async toggleLike() {
      // 模拟API请求延迟
      await new Promise(resolve => setTimeout(resolve, 300));

      this.isLiked = !this.isLiked;
      this.likeCount += this.isLiked ? 1 : -1;
    },

    async fetchLikeCount() {
      // 模拟API请求延迟
      await new Promise(resolve => setTimeout(resolve, 300));

      this.likeCount = Math.floor(Math.random() * 100);
    },

    async checkIfLiked() {
      // 模拟API请求延迟
      await new Promise(resolve => setTimeout(resolve, 300));

      this.isLiked = Math.random() < 0.5;
    },

    async fetchComments() {
      // 模拟API请求延迟
      await new Promise(resolve => setTimeout(resolve, 300));

      // 模拟评论数据
      this.comments = [
        { commentId: 'comment1', content: '这是一条测试评论1', createdAt: "2023-07-07T10:00:00Z", createdBy: { username: 'User1' } },
        { commentId: 'comment2', content: '这是一条测试评论2', createdAt: "2023-07-07T11:00:00Z", createdBy: { username: 'User2' } },
        { commentId: 'comment3', content: '这是一条测试评论3', createdAt: "2023-07-07T12:00:00Z", createdBy: { username: 'User3' } },
      ];
    },

    async postComment() {
      // 模拟API请求延迟
      await new Promise(resolve => setTimeout(resolve, 300));

      const newCommentObj = {
        commentId: 'comment' + (this.comments.length + 1),
        content: this.newComment,
        createdAt: new Date().toISOString(),
        createdBy: { username: this.username }
      };

      this.comments.push(newCommentObj);
      this.newComment = '';
    },

    async deleteComment(commentId) {
      // 模拟API请求延迟
      await new Promise(resolve => setTimeout(resolve, 300));

      this.comments = this.comments.filter(comment => comment.commentId !== commentId);
    },

    canDeleteComment(comment) {
      return this.note.username === this.username || comment.createdBy.username === this.username;
    },

      formatDate(dateString) {
        return new Date(dateString).toLocaleString();
      },

    },
    async mounted() {
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