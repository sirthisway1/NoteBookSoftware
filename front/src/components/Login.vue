<!-- 已测试接口 -->
<template>
  <div class="login-container">
    <div class="login-box">
      <h2>登录</h2>
      <form @submit.prevent="submitForm">
        <div class="input-group">
          <label for="username">用户名</label>
          <input type="text" id="username" v-model="username" required minlength="3" maxlength="20">
        </div>
        <div class="input-group">
          <label for="password">密码</label>
          <input type="password" id="password" v-model="password" required minlength="6" maxlength="20">
        </div>
        <div class="button-group">
          <button type="button" @click="register()">注册账号</button>
          <button type="submit">确认登录</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { onMounted, onUnmounted } from 'vue';
import effect from '@/assets/effect.js';
import { ElMessage } from 'element-plus'

export default {
  setup() {
    const { start, stop } = effect();
    onMounted(() => start());
    onUnmounted(() => stop());
  },
  data() {
    return {
      username: '',
      password: ''
    };
  },
  methods: {
    async submitForm() {
      if (this.validateForm()) {
        await this.login();
      }
    },
    validateForm() {
      const usernameLength = this.username.length;
      const passwordLength = this.password.length;

      if (usernameLength < 3 || usernameLength > 20) {
        alert('用户名必须在3到20个字符之间');
        return false;
      }

      if (passwordLength < 6 || passwordLength > 20) {
        alert('密码必须在6到20个字符之间');
        return false;
      }

      return true;
    },
    async login() {
      try {
        const response = await axios.post('/api/login', {
          username: this.username,
          password: this.password
        });

        const data = response.data;
        if (data.code === "200") {
          ElMessage({
            duration:1000,
            message: '登录成功',
            type: 'success',
          })
          // 处理登录成功后的逻辑，保存token
          localStorage.setItem('token', data.data.token);
          this.$router.push({ name: 'Start' }); // 登录成功后跳转到 Start 页面
        } 
      } catch (error) {
        console.error('登录失败:', error);
        if (error.response) {
          ElMessage({
            duration:1000,
            message: '登录失败'+error.response.data.message,
            type: 'error',
          })
        }
      }
    },
    register() {
      this.$router.push('/register');
    },
    
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-family: "宋体", sans-serif;
}

.login-box {
  width: 320px;
  padding: 30px;
  border: 1px solid #ccc;
  border-radius: 15px;
  background-color: #fff;
  text-align: center;
}

h2 {
  margin-bottom: 20px;
  font-size: 24px;
}

.input-group {
  margin-bottom: 20px;
  text-align: left;
}

label {
  display: block;
  margin-bottom: 10px;
  font-size: 18px;
}

input {
  width: calc(100% - 25px);
  padding: 10px;
  font-size: 18px;
  border: none;
  border-radius: 15px;
  background-color: #eee;
}

.button-group {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
}

button {
  padding: 10px 20px;
  font-size: 18px;
  color: #fff;
  background-color: #555;
  border: none;
  border-radius: 15px;
  cursor: pointer;
  transition: background-color 0.3s ease, opacity 0.3s ease;
  font-family: "宋体", sans-serif;
}

button:hover {
  background-color: #64ffc1; /* 浅绿色 */
  opacity: 0.8;
}
</style>