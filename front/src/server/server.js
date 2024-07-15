const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const app = express();
const multer = require('multer');
const path = require('path');
const { users, notebooks, notes,likes, comments } = require('./data');

app.use(bodyParser.json());
app.use(cors());

const uploadsPath = path.join(__dirname, 'uploads');
app.use('/uploads', express.static(uploadsPath));

console.log('Uploads directory path:', uploadsPath);




// 模拟身份验证中间件
const authMiddleware = (req, res, next) => {
  const authHeader = req.headers.authorization;
  if (authHeader && authHeader.startsWith('Bearer ')) {
    const token = authHeader.substring(7); // Remove "Bearer " from the start
    // console.log('token:', token);
    if (token.startsWith('fake-jwt-token-')) {
      next();
    } else {
      res.status(401).json({ code: 501, message: 'Invalid token' });
    }
  } else {
    res.status(401).json({ code: 501, message: 'No token provided' });
  }
};

//注册
app.post('/api/register', (req, res) => {
  const { email, username, password } = req.body;
  if (users.some(user => user.username === username)) {
    return res.status(400).json({ code: 502, message: '用户名已存在' });
  }

  if (users.some(user => user.email === email)) {
    return res.status(400).json({ code: 503, message: '邮箱已存在' });
  }

  users.push({ email, username, password });
  res.status(200).json({ code: 200, message: '注册成功' });
 }
);

//登录
app.post('/api/login', (req, res) => {
  const { username, password } = req.body;
  const user = users.find(u => u.username === username && u.password === password);

  if (user) {
    res.json({
      code: 200,
      message: '登录成功',
      data: {
        token: 'fake-jwt-token-' + username
      }
    });
  } else {
    res.status(400).json({
      code: 504,
      message: '用户名或密码错误'
    });
  }
  // console.log('token:', res.token);
});

// 获取笔记列表
app.get('/api/notes', authMiddleware, (req, res) => {
  const { keyword, tags } = req.query;
  let filteredNotes = notes;

  if (keyword) {
    filteredNotes = filteredNotes.filter(note => 
      note.title.toLowerCase().includes(keyword.toLowerCase()) || 
      note.content.toLowerCase().includes(keyword.toLowerCase())
    );
  }

  if (tags) {
    const tagList = tags.split(',');
    filteredNotes = filteredNotes.filter(note => 
      tagList.some(tag => note.tags.includes(tag))
    );
  }

  if (filteredNotes.length > 0) {
    res.json({ code: 200, data: filteredNotes });
  } else {
    res.json({ code: 407, message: '没有找到匹配的笔记' });
  }
});

//获取所有笔记本ID
app.get('/api/notebooks/getAllId', authMiddleware, (req, res) => {
  const notebookIds = notebooks.map(notebook => notebook.notebookId);
  console.log('Sending notebook IDs:', notebookIds); // 添加这行日志
  res.json({ code: 200, data: { notebookIds } });
});

//获取单个笔记本详情的接口：
app.get('/api/notebooks/:notebookId', authMiddleware, (req, res) => {
  const notebookId = parseInt(req.params.notebookId);
  const notebook = notebooks.find(nb => nb.notebookId === notebookId);
  if (!notebook) {
    return res.status(404).json({ code: 404, message: "Notebook not found" });
  }
  const notebookNotes = notes.filter(note => note.notebookId === notebookId);
  res.json({ 
    code: 200, 
    data: {
      ...notebook,
      notes: notebookNotes
    }
  });
});

// 创建笔记本
app.post('/api/notebooks/createNotebooks', authMiddleware, (req, res) => {
  const { name, summary } = req.body;
  if (name && summary) {
    const newNotebook = {
      notebookId: notebooks.length + 1,
      name,
      summary,
      lastModified: new Date().toISOString()
    };
    notebooks.push(newNotebook);
    res.json({ code: 200, message: '笔记本创建成功', data: newNotebook });
  } else {
    res.status(400).json({ code: 400, message: '创建笔记本失败' });
  }
});

app.get('/api/notebooks', authMiddleware, (req, res) => {
  res.json({ code: 200, data: notebooks });
});

// 创建笔记
app.post('/api/notes', authMiddleware, (req, res) => {
  const { title, content = "你好", tags, notebookId } = req.body;
  if (!title || !notebookId) {
    return res.status(400).json({ code: 400, message: '缺少必要参数' });
  }

  const notebook = notebooks.find(nb => nb.notebookId === parseInt(notebookId));
  if (!notebook) {
    return res.status(404).json({ code: 404, message: '笔记本不存在' });
  }

  const newNote = {
    noteId: notes.length + 1,
    title,
    content, // 这里会使用提供的 content，或者默认值 "你好"
    createdAt: new Date().toISOString(),
    updatedAt:  new Date().toISOString(),
    tags: tags || [], // 保留 tags 处理
    notebookId: parseInt(notebookId)
  };
  notes.push(newNote);
  res.json({ code: 200, message: '笔记创建成功', data: newNote });
});

//修改笔记本名称或简介的接口
app.put('/api/notebooks/:notebookId', authMiddleware, (req, res) => {
  const notebookId = parseInt(req.params.notebookId);
  const { title, summary } = req.body;
  const notebookIndex = notebooks.findIndex(nb => nb.notebookId === notebookId);
  
  if (notebookIndex !== -1) {
    if (title) notebooks[notebookIndex].name = title;
    if (summary) notebooks[notebookIndex].summary = summary;
    notebooks[notebookIndex].lastModified = new Date().toISOString();
    res.json({ code: 200, message: '笔记本更新成功', data: notebooks[notebookIndex] });
  } else {
    res.status(404).json({ code: 404, message: '笔记本不存在' });
  }
});

//删除笔记本的接口
app.delete('/api/notebooks/:notebookId', authMiddleware, (req, res) => {
  const notebookId = parseInt(req.params.notebookId);
  const notebookIndex = notebooks.findIndex(nb => nb.notebookId === notebookId);
  
  if (notebookIndex !== -1) {
    notebooks.splice(notebookIndex, 1);
    res.json({ code: 200, message: '笔记本删除成功' });
  } else {
    res.status(404).json({ code: 404, message: '笔记本不存在' });
  }
});

// 修改后的路由：获取所有公开笔记（用于社区展示）
app.get('/api/community-notes', authMiddleware, (req, res) => {
  const { keyword, tags, username } = req.query;
  let filteredNotes = notes.filter(note => !note.isPrivate); // 只返回公开笔记

  if (keyword) {
    filteredNotes = filteredNotes.filter(note => 
      note.title.toLowerCase().includes(keyword.toLowerCase()) || 
      note.content.toLowerCase().includes(keyword.toLowerCase())
    );
  }

  if (tags) {
    const tagList = tags.split(',');
    filteredNotes = filteredNotes.filter(note => 
      tagList.some(tag => note.tags.includes(tag))
    );
  }

  if (username) {
    filteredNotes = filteredNotes.filter(note => note.username === username);
  }

  // 按创建时间降序排序
  filteredNotes.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

  res.json({ 
    code: 200, 
    data: filteredNotes,
    total: filteredNotes.length
  });
});

//获取单个笔记详情：
app.get('/api/notes/:noteId', authMiddleware, (req, res) => {
  const noteId = parseInt(req.params.noteId);
  const note = notes.find(n => n.noteId === noteId);
  if (note) {
    res.json({ code: 200, data: note });
  } else {
    res.status(404).json({ code: 404, message: '笔记不存在' });
  }
});

//更新笔记内容：
app.put('/api/notes/:noteId', authMiddleware, (req, res) => {
  const noteId = parseInt(req.params.noteId);
  const { title, content, tags, isPrivate } = req.body;
  const noteIndex = notes.findIndex(n => n.noteId === noteId);
  
  if (noteIndex !== -1) {
    if (title) notes[noteIndex].title = title;
    if (content) notes[noteIndex].content = content;
    if (tags) notes[noteIndex].tags = tags;
    if (isPrivate !== undefined) notes[noteIndex].isPrivate = isPrivate;
    notes[noteIndex].updatedAt = new Date().toISOString();
    res.json({ code: 200, message: '笔记更新成功', data: notes[noteIndex] });
  } else {
    res.status(404).json({ code: 404, message: '笔记不存在' });
  }
});

//删除笔记
app.delete('/api/notes/:noteId', authMiddleware, (req, res) => {
  const noteId = parseInt(req.params.noteId);
  const noteIndex = notes.findIndex(n => n.noteId === noteId);
  
  if (noteIndex !== -1) {
    notes.splice(noteIndex, 1);
    res.json({ code: 200, message: '笔记删除成功' });
  } else {
    res.status(404).json({ code: 404, message: '笔记不存在' });
  }
});

// 获取用户信息
app.get('/api/user', authMiddleware, (req, res) => {
  const authHeader = req.headers.authorization;
  
  const token = authHeader.split(' ')[1]; // 获取 token
  // console.log('token:', req.token);
  const username = decodeURIComponent(token.split('-').pop()); // 从 token 中提取用户名
  // console.log('Received request for user info');
  // console.log('Username from request:', username);
  const user = users.find(u => u.username === username);
  if (user) {
    const { password, ...userInfo } = user; // 不返回密码
    res.json({ code: 200, data: userInfo });
  } else {
    res.status(404).json({ code: 404, message: '用户不存在' });
  }
});

//更新用户信息
app.put('/api/user', authMiddleware, (req, res) => {
  const username = req.headers.authorization.split('-').pop();
  const { email, newPassword } = req.body;
  const userIndex = users.findIndex(u => u.username === username);
  
  if (userIndex !== -1) {
    if (email) users[userIndex].email = email;
    if (newPassword) users[userIndex].password = newPassword;
    res.json({ code: 200, message: '用户信息更新成功' });
  } else {
    res.status(404).json({ code: 404, message: '用户不存在' });
  }
});

//获取笔记本中的所有笔记：
app.get('/api/notebooks/:notebookId/notes', authMiddleware, (req, res) => {
  const notebookId = parseInt(req.params.notebookId);
  const notebookNotes = notes.filter(note => note.notebookId === notebookId);
  res.json({ code: 200, data: notebookNotes });
});

//获取评论
app.get('/api/comments/view/:noteId', authMiddleware, (req, res) => {
  const noteId = parseInt(req.params.noteId);
  const noteComments = comments.filter(comment => comment.noteId === noteId);
  res.json({ code: 200, data: noteComments });
});

//发表评论
app.post('/api/comments/post', authMiddleware, (req, res) => {
  const { noteId, content } = req.body;
  const username = req.headers.authorization.split('-').pop(); // 不解码，保持原始格式
  console.log('用户名（发表评论）：',username);
  const newComment = {
    commentId: `comment${comments.length + 1}`,
    noteId: parseInt(noteId),
    content,
    createdAt: new Date().toISOString(),
    createdBy: { username:username }
  };
  comments.push(newComment);
  res.json({ code: 200, message: '评论发表成功', data: newComment });
});

//解码姓名
function normalizeUsername(username) {
  try {
    return decodeURIComponent(username).trim().toLowerCase();
  } catch (e) {
    console.error("Error decoding username:", e);
    return username.trim().toLowerCase();
  }
}

//删除评论
app.delete('/api/comments/delete/:noteId/:commentId', authMiddleware, (req, res) => {
  const { noteId, commentId } = req.params;
  const username = req.headers.authorization.split('-').pop(); // 获取编码后的用户名
  
  console.log('用户名（删除评论）：', username);
  const commentIndex = comments.findIndex(c => c.commentId === commentId && c.noteId === parseInt(noteId));
  
  if (commentIndex !== -1) {
    const comment = comments[commentIndex];
    const note = notes.find(n => n.noteId === parseInt(noteId));
    
    console.log('评论创建者：', comment.createdBy.username);
    console.log('笔记创建者：', note.username);
    console.log('当前用户：', username);

    if (normalizeUsername(comment.createdBy.username) === normalizeUsername(username) || 
        normalizeUsername(note.username) === normalizeUsername(username)) {
      comments.splice(commentIndex, 1);
      res.json({ code: 200, message: '评论删除成功' });
    } else {
      res.status(403).json({ code: 403, message: '没有权限删除此评论' });
    }
  } else {
    res.status(404).json({ code: 404, message: '评论不存在' });
  }
});

// 切换点赞状态
app.post('/api/noteLikes/toggleLike', authMiddleware, (req, res) => {
  const { noteId } = req.body;
  const username = req.headers.authorization.split('-').pop();
  const likeIndex = likes.findIndex(like => like.noteId === parseInt(noteId) && like.username === username);
  
  if (likeIndex !== -1) {
    likes.splice(likeIndex, 1);
  } else {
    likes.push({ noteId: parseInt(noteId), username });
  }
  
  const likeCount = likes.filter(like => like.noteId === parseInt(noteId)).length;
  const isLiked = likeIndex === -1;
  
  res.json({ code: 200, data: { isLikedByCurrentUser: isLiked, likes: likeCount } });
});

// 获取点赞数
app.get('/api/noteLikes/count/:noteId', authMiddleware, (req, res) => {
  const noteId = parseInt(req.params.noteId);
  const likeObj = likes.find(like => like.noteId === noteId);
  const likeCount = likeObj ? likeObj.count : 0;
  res.json({ code: 200, count: likeCount });
});

// 检查当前用户是否已点赞
app.get('/api/noteLikes/isLiked/:noteId', authMiddleware, (req, res) => {
  const noteId = parseInt(req.params.noteId);
  const username = req.headers.authorization.split('-').pop();
  const isLiked = likes.some(like => like.noteId === noteId && like.username === username);
  res.json({ code: 200, Liked: isLiked });
});

// 配置 multer 用于文件上传
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, uploadsPath) // 确保这个目录存在
  },
  filename: function (req, file, cb) {
    cb(null, Date.now() + path.extname(file.originalname)) // 生成唯一的文件名
  }
});

const upload = multer({ storage: storage });

// 确保上传目录存在
const fs = require('fs');
if (!fs.existsSync(uploadsPath)){fs.mkdirSync(uploadsPath, { recursive: true });}

// 打印上传路径以进行调试
console.log('Files will be uploaded to:', uploadsPath);

// 处理图片上传的路由
app.post('/api/upload', upload.single('image'), (req, res) => {
  if (!req.file) {
    return res.status(400).json({ errno: 1, message: "No file uploaded" });
  }

  
  const host = req.get('host');
  const fullUrl = `${req.protocol}://${host}/uploads/${req.file.filename}`;

  // 构造返回的数据
  const responseData = {
    errno: 0,
    data: {
      url: fullUrl, // 图片 src
    }
  };
  
  res.json(responseData);
});

app.listen(3000, () => console.log('Server running on port 3000'));