<template>
    <transition name="fade">
      <div v-if="visible" class="tag-modal-overlay">
        <div class="modal-content">
          <h2>添加标签</h2>
          <div class="input-group">
            <input v-model="newTag" placeholder="输入新标签" @keyup.enter="addTag" />
            <button @click="addTag(newTag)" class="add-btn">添加</button>
          </div>
          <h3>当前标签:</h3>
          <ul class="tag-list">
            <li v-for="tag in localTags" :key="tag">
              {{ tag }}
              <button @click="removeTag(tag)" class="remove-btn">X</button>
            </li>
          </ul>
          <button @click="close" class="close-btn">关闭</button>
        </div>
      </div>
    </transition>
  </template>
  
  <script>
  import axios from 'axios';
  export default {
    props: {
      visible: Boolean,
      tags: Array,
      selectedNote: Object,
    },
    data() {
      return {
        newTag: '',
        localTags: [],
      };
    },
    watch: {
    tags: {
        immediate: true,
        handler(newTags) {
          this.localTags = Array.isArray(newTags) ? [...newTags] : [];
        },
       
    },
    },
    methods: {
      async addTag(tag) {
        const token = localStorage.getItem('token');
              if (!token) {
                alert('请先登录');
                this.$router.push('/login');
                return;
              }
          try {
            const response = await axios.post('/api/notes/addTags', null, {
              params: {
                noteId: this.selectedNote.noteId,
                tags: tag
              },
              headers: { 
                  token: token
                }
            });

            if (response.data.code === "200") {
              this.localTags.push(tag);
              this.newTag = ''; // 清空输入框
              this.$emit('update:tags', this.localTags); // 通知父组件更新标签
              alert('标签添加成功');
            } else {
              alert(`添加标签失败: ${response.data.message}`);
            }
          } catch (error) {
            console.error('Error adding tag:', error);
            alert('添加标签失败');
          }
        },
    async removeTag(tag) {
      const token = localStorage.getItem('token');
              if (!token) {
                alert('请先登录');
                this.$router.push('/login');
                return;
              }
        try {
          const response = await axios.post('/api/notes/removeTags', null, {
            params: {
              noteId: this.selectedNote.noteId,
              tags: tag
            },
            headers: { 
                  token: token
                }
          });

          if (response.data.code === "200") {
            const index = this.selectedNote.tags.indexOf(tag);
            if (index > -1) {
              this.localTags.splice(index, 1);
            }
            this.$emit('update:tags', this.localTags);
            alert('标签移除成功');
          } else {
            alert(`移除标签失败: ${response.data.message}`);
          }
        } catch (error) {
          console.error('Error removing tag:', error);
          alert('移除标签失败');
        }
      },
      close() {
      this.$emit('close');
    },
    },
  };
  </script>
  
  <style scoped>
.tag-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #ffffff;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  max-width: 400px;
  width: 100%;
}

h2 {
  color: #333;
  margin-bottom: 1rem;
}

.input-group {
  display: flex;
  margin-bottom: 1rem;
}

input {
  flex-grow: 1;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-right: 0.5rem;
}

.add-btn {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}

h3 {
  color: #666;
  margin-bottom: 0.5rem;
}

.tag-list {
  list-style-type: none;
  padding: 0;
  margin-bottom: 1rem;
}

.tag-list li {
  background-color: #f0f0f0;
  color: #333;
  padding: 0.3rem 0.6rem;
  margin-bottom: 0.3rem;
  border-radius: 3px;
  display: inline-block;
  margin-right: 0.3rem;
}

.close-btn {
  background-color: #f44336;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  width: 100%;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}
</style>