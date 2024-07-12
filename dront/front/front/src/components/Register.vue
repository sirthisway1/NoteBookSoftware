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

export default {
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

        const data = response.data;
        if (data.code === 200) {
          alert('注册成功');
          this.$router.push('/login');
        } else if (data.code === 502) {
          alert('用户名已存在');
        } else if (data.code === 503) {
          alert('邮箱已存在');
        } else {
          alert('注册失败');
        }
      } catch (error) {
        console.error('注册失败:', error);
        alert('注册失败');
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
  background-color: #f5f5f5;
  font-family: "宋体", sans-serif;
}

.register-box {
  width: 300px;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 10px;
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
  margin-bottom: 5px;
  font-size: 14px;
}

input {
  width: 95%;
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.button-group {
  display: flex;
  justify-content: space-between;
}

button {
  padding: 10px 20px;
  font-size: 14px;
  color: #000000;
  background-color: #aaa;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-family: "宋体", sans-serif;
}

button:first-child {
  background-color: #aaa;
}

button:hover {
  /* opacity: 0.8; */
  background-color: #64ffc1;
}
</style>
