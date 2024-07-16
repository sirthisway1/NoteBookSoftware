<template>
  <div class="container">
    <!-- Sidebar remains unchanged -->
    <div class="sidebar">
      <div class="sidebar-user">
        <img :src="user.avatar || 'default-avatar.png'" alt="User Avatar" class="sidebar-avatar">
        <div class="sidebar-username" id="username">{{ username }}</div>
      </div>
      <div class="sidebar-item" @click="goToStart">开始</div>
      <div class="sidebar-item" @click="goToNotebook">笔记本</div>
      <div class="sidebar-item" @click="goToCommunity">发现社区</div>
      <div class="sidebar-item active" @click="goToUserCenter">用户中心</div>
    </div>
    <div class="main-content">
      <div class="user-center">
        <h1>用户中心</h1>
        <div class="avatar-container">
          <img :src="user.avatar || 'default-avatar.png'" alt="User Avatar" class="avatar-image">
          <input type="file" ref="fileInput" @change="onFileSelected" accept="image/*" style="display:none">
          <button @click="$refs.fileInput.click()" class="upload-button">上传图片</button>
        </div>
        <div class="user-info">
          <div class="info-item">
            <label>姓名：</label>
            <input v-model="user.username" :disabled="!editingName" />
            <button @click="toggleEdit('name')">{{ editingName ? '确认' : '修改' }}</button>
          </div>
          <div class="info-item">
            <label>邮箱：</label>
            <input v-model="user.email" :disabled="!editingEmail" />
            <button @click="toggleEdit('email')">{{ editingEmail ? '确认' : '修改' }}</button>
          </div>
          <div class="info-item">
            <label>密码：</label>
            <input v-model="user.password"  :disabled="!editingPassword" />
            <button @click="toggleEdit('password')">{{ editingPassword ? '确认' : '修改' }}</button>
          </div>
          <div class="info-item">
            <label>简介：</label>
            <textarea v-model="user.bio" :disabled="!editingBio"></textarea>
            <button @click="toggleEdit('bio')">{{ editingBio ? '确认' : '修改' }}</button>
          </div>
        </div>
        <div class="button-container">
          

          
          <button class="action-button save-button" @click="updateUserData">保存所有修改</button>
          <button class="action-button logout-button" @click="showConfirmModal = true">退出登录</button>
        </div>
      </div>
    </div>

    
    <ConfirmModal
          v-if="showConfirmModal"
          :isVisible="showConfirmModal"
          message="是否确认退出登录？"
          @confirm="handleConfirmLogout"
          @cancel="handleCancelLogout"
        />


  </div>
</template>

<script>
import axios from 'axios';
import ConfirmModal from './ConfirmModal.vue';

export default {
  data() {
    return {
      username: '',
      user: {
        username: '',
        email: '',
        password: '',
        bio: '',
        avatar: ''
      },
      editingName: false,
      editingEmail: false,
      editingPassword: false,
      editingBio: false,
      originalUser: {}, // 用于存储原始用户数据
      showConfirmModal: false
    };
  },
  components: {
    ConfirmModal
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
      // 已在用户中心页面，无需操作
    },
    async goToLogin() {
      try {
        // 2. 清除本地存储的登录信息
        localStorage.removeItem('token');
        
        // 3. 跳转到登录页面
        this.$router.push({ name: 'Login' });
      } catch (error) {
        console.error('登出失败:', error);
        // 即使 API 调用失败，也清除本地登录信息并跳转
        localStorage.removeItem('token');
        this.$router.push({ name: 'Login' });
      }
    },

    handleConfirmLogout() {
      this.showConfirmModal = false;
      this.goToLogin();
    },

    handleCancelLogout() {
      this.showConfirmModal = false;
    },
    showNameModal(){
      this.showNameDialog = true;
    },
    showEmailModal() {
      this.showEmailDialog = true;
    },
    showPasswordModal() {
      this.showPasswordDialog = true;
    },
    showBioModal() {
      this.showPasswordDialog = true;
    },
    async fetchCurrentUser() {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('请先登录');
        this.$router.push('/login');
        return;
      }

      try {
        const response = await axios.get('/api/user', {
          headers: { token: token }
        });
        if (response.data && response.data.code === "200") {
          this.currentUser = response.data.data;
          this.username = this.currentUser.username;
          this.user.username = this.username;
          this.user.email = this.currentUser.email;
          this.user.password = this.currentUser.password;
          this.user.bio = this.currentUser.bio;
          this.user.avatar = this.currentUser.avatar;
          console.log('用户名：', this.username);
          this.originalUser = { ...this.user };
        }
      } catch (error) {
        console.error("Error fetching current user:", error);
      }
    },

    onFileSelected(event) {
      const file = event.target.files[0];
      if (file) {
        this.uploadAvatar(file);
      }
    },

    async uploadAvatar(file) {
      const formData = new FormData();
      formData.append('file', file);

      try {
        const response = await axios.post('/api/user/avatar', formData, {
          headers: { 
            'Content-Type': 'multipart/form-data',
            token: localStorage.getItem('token')
          }
        });

        if (response.data && response.data.code === "200") {
          alert('头像上传成功');
          // 刷新用户信息
          this.fetchCurrentUser();
        }
      } catch (error) {
        console.error("Error uploading avatar:", error);
        alert('头像上传失败');
      }
    },

    toggleEdit(field) {
      this[`editing${field.charAt(0).toUpperCase() + field.slice(1)}`] = !this[`editing${field.charAt(0).toUpperCase() + field.slice(1)}`];
    },

    //保存用户信息
    async updateUserData() {
      const updatedFields = {};
      for (const key in this.user) {
        if (this.user[key] !== this.originalUser[key]) {
          updatedFields[key] = this.user[key];
        }
      }

      if (Object.keys(updatedFields).length === 0) {
        alert('没有需要更新的信息');
        return;
      }

      try {
        const response = await axios.put('/api/user/update', updatedFields, {
          headers: { token: localStorage.getItem('token') }
        });
        if (response.data && response.data.code === "200") {
          alert('用户信息更新成功');
          this.originalUser = { ...this.user };
          this.username = this.user.username;
        }
      } catch (error) {
        console.error("Error updating user data:", error);
        alert('用户信息更新失败');
      }
    }
  },
  async mounted() {
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

.sidebar-item:hover, .sidebar-item.active {
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


.main-content {
  flex: 1;
  padding: 40px;
  overflow-y: auto;
  background-color: #f5f7fa;
}

.user-center {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.user-center h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 30px;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
}

.avatar-image {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 15px;
  border: 4px solid #4CAF50;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.upload-button {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s, transform 0.2s;
}

.upload-button:hover {
  background-color: #45a049;
  transform: translateY(-2px);
}

.user-info {
  background-color: #f9f9f9;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  width: 100%;
}

.info-item {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.info-item label {
  font-weight: bold;
  margin-right: 15px;
  min-width: 80px;
  color: #555;
}

.info-item input, .info-item textarea {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.info-item input:focus, .info-item textarea:focus {
  border-color: #4CAF50;
  outline: none;
}

.info-item textarea {
  height: 100px;
  resize: vertical;
}

.info-item button {
  margin-left: 15px;
  padding: 8px 15px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.info-item button:hover {
  background-color: #45a049;
}

/* .save-all-button {
  
  margin-top: 30px;
  padding: 12px 25px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s, transform 0.2s;
  
}

.save-all-button:hover {
  background-color: #45a049;
  transform: translateY(-2px);
} */


/* 按钮 */
.button-container {
  display: flex;
  justify-content: space-between;
  width: 100%;
  margin-top: 30px;
}

.action-button {
  padding: 12px 25px;
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s, transform 0.2s;
  flex: 1;
  margin: 0 10px;
}

.action-button:hover {
  transform: translateY(-2px);
}

.save-button {
  background-color: #4CAF50;
}

.save-button:hover {
  background-color: #45a049;
}

.logout-button {
  background-color: #4CAF50;
}

.logout-button:hover {
  background-color: #45a049;
}

@media (max-width: 768px) {
  .main-content {
    padding: 20px;
  }

  .user-center {
    padding: 20px;
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .info-item label {
    margin-bottom: 5px;
  }

  .info-item input, .info-item textarea, .info-item button {
    width: 100%;
    margin-left: 0;
    margin-top: 5px;
  }
}
</style>