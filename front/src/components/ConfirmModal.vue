<template>
    <div class="modal-overlay" v-if="isVisible">
      <div class="modal-content">
        <p class="modal-message">{{ message }}</p>
        <div class="modal-buttons">
          <button class="modal-button confirm-button" @click="confirmAction">是</button>
          <button class="modal-button cancel-button" @click="cancelAction">否</button>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      isVisible: {
        type: Boolean,
        required: true
      },
      message: {
        type: String,
        required: true
      }
    },
    methods: {
      confirmAction() {
        this.$emit('confirm');
      },
      cancelAction() {
        this.$emit('cancel');
      }
    }
  };
  </script>
  
  <style scoped>
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }
  
  .modal-content {
    background: #fff;
    padding: 40px;
    border-radius: 12px;
    text-align: center;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }
  
  .modal-message {
    font-size: 18px;
    color: #333;
    margin-bottom: 30px;
  }
  
  .modal-buttons {
    display: flex;
    justify-content: center;
    gap: 40px; /* 增加按钮之间的距离 */
  }
  
  .modal-button {
    padding: 10px 40px; /* 使按钮变长 */
    border: none;
    border-radius: 25px;
    cursor: pointer;
    font-size: 16px;
    transition: background-color 0.3s, transform 0.2s;
  }
  
  .confirm-button {
    background-color: #4CAF50;
    color: white;
  }
  
  .confirm-button:hover {
    background-color: #45a049;
    transform: translateY(-2px);
  }
  
  .cancel-button {
    background-color: #f44336;
    color: white;
  }
  
  .cancel-button:hover {
    background-color: #e53935;
    transform: translateY(-2px);
  }
  </style>
  