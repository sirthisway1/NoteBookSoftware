<!-- 已测试接口 -->
<template>
  <div class="register-container">
    <div class="register-box">
      <h2>注册</h2>
      <form @submit.prevent="submitForm">
        <div class="input-group">
          <label for="email">邮箱</label>
          <input type="email" id="email" v-model="email" required>
        </div>
        <div class="input-group">
          <label for="username">用户名</label>
          <input type="text" id="username" v-model="username" required minlength="3" maxlength="20">
        </div>
        <div class="input-group">
          <label for="password">密码</label>
          <input type="password" id="password" v-model="password" required minlength="6" maxlength="20">
        </div>
        <div class="button-group">
          <button type="button" @click="goBack">返回登录</button>
          <button type="submit">确认注册</button>
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

    // 在组件挂载时启动特效
    onMounted(() => start());

    // 在组件卸载时停止特效
    onUnmounted(() => stop());
  },
  data() {
    return {
      email: '',
      username: '',
      password: ''
    };
  },
  methods: {
    async submitForm() {
      if (this.validateForm()) {
        await this.register();
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
    async register() {
      try {
        const response = await axios.post('/api/register', {
          email: this.email,
          username: this.username,
          password: this.password
        });
        console.log("111111111");
        const data = response.data;
        if (data.code === "200") {
          ElMessage({
            duration:1000,
            message: '注册成功',
            type: 'success',
          })
          this.$router.push('/login');
        } 
      } catch (error) {
        console.error('注册失败:', error);
        if (error.response) {
          ElMessage({
            duration:1000,
            message: '注册失败'+error.response.data.message,
            type: 'error',
          })
        }
      }
    },
    goBack() {
      this.$router.push('/login');
    }
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-family: "宋体", sans-serif;
}


.register-box {
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
  margin-bottom: 15px;
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
