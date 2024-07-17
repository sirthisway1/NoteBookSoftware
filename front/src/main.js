import { createApp } from 'vue';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue';
import { QuillEditor } from '@vueup/vue-quill';
import 'quill/dist/quill.snow.css';
import { createRouter, createWebHistory } from 'vue-router';
import Login from './components/Login.vue';
import Register from './components/Register.vue';
import Start from './components/Start.vue';  
import Notebook from './components/Notebook.vue';
import NotebookDetail from './components/NotebookDetail.vue';
import NoteCreate from './components/NoteCreate.vue';
import NoteDetail from './components/NoteDetail.vue';
import NoteEdit from './components/NoteEdit.vue';
import NoteCreateTree from './components/NoteCreateTree.vue';
import UserCenter from './components/UserCenter.vue';
import Community from './components/Community.vue';
import CommunityDetail from './components/CommunityDetail.vue'
import '@fortawesome/fontawesome-free/css/all.css';
// import FavoriteNotes from './components/FavoriteNotes.vue';
import PrivateNotes from './components/PrivateNotes.vue';
import MindnoteEdit from './components/MindnoteEdit.vue';
import axios from 'axios';

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login,name: 'Login' },
  { path: '/register', component: Register },
  { path: '/start', component: Start, name: 'Start' },
  { path: '/notebook', component: Notebook, name: 'Notebook' },
  { path: '/community', component: Community, name: 'Community' },
  { path: '/usercenter', component: UserCenter, name: 'UserCenter' },

  { path: '/notebook/:notebookId', component: NotebookDetail, name: 'NotebookDetail' }, // 添加动态路由
  { path: '/notebook/:notebookId/create', component: NoteCreate, name: 'NoteCreate' }, // 添加“添加新笔记”界面路由
  { path: '/notebook/:notebookId/note/:noteId', component: NoteDetail, name: 'NoteDetail' }, // 添加NoteDetail的动态路由
  { path: '/notebook/:notebookId/note/:noteId/edit', component: NoteEdit, name: 'NoteEdit' }, // 添加NoteEdit的动态路由
  { path: '/notebook/:notebookId/createTree', component: NoteCreateTree, name: 'NoteCreateTree' }, //思维笔记
  { path: '/community/:noteId/:username', component: CommunityDetail, name: 'CommunityDetail' }, //发现社区跳转后的页面
  { path: '/private',component: PrivateNotes  ,name: 'PrivateNotes' },
  { path: '/notebook/:notebookId/note/:noteId/mindnoteedit', component: MindnoteEdit, name: 'MindnoteEdit' },

  
];

axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${encodeURIComponent(token)}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

const router = createRouter({
  history: createWebHistory(),
  routes
});

const app = createApp(App);
app.use(router);
app.use(ElementPlus);
app.component('QuillEditor', QuillEditor);
app.mount('#app');
// // 获取私密笔记列表
// app.get('/api/notes/private', (req, res) => {
//   // 根据用户 ID 获取私密笔记
// });
