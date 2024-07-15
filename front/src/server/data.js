  //用户
  let users = [
    {
      email: "tzcFf3@example.com",
      username:"原神孝子",
      password:"123456"
    },
    {
      email: "mR7X1z@example.com",
      username:"方舟孝子",
      password:"123456"
    },
    {
      email: "6GdnE2@example.com",
      username:"鸣潮孝子",
      password:"123456"
    },
    {
      email: "W1sPk9@example.com",
      username:"贴吧吧友",
      password:"123456"
    },
  ];
  //笔记
  let notes = [
    {
      username:"贴吧吧友",
      noteId: 1,
      title: "JavaScript基础",
      content: "JavaScript是一种轻量级的编程语言...",
      createdAt: "2023-05-01T12:00:00Z",
      updatedAt:  "2024-07-01T10:00:00Z",
      tags: ["编程", "Web开发"],
      notebookId: 1,
      type:0,
      isPrivate: true
    },
    {
      username:"鸣潮孝子",
      noteId: 2,
      title: "Vue.js入门",
      content: "Vue.js是一个用于构建用户界面的渐进式框架...",
      createdAt: "2023-05-01T12:00:00Z",
      updatedAt: "2024-07-02T14:30:00Z",
      tags: ["前端", "框架"],
      notebookId: 1,
      type:0,
      isPrivate: false
    },
    {
      username:"原神孝子",
      noteId: 3,
      title: "健康饮食指南",
      content: "均衡的饮食对于保持身体健康至关重要...",
      createdAt: "2023-05-01T12:00:00Z",
      updatedAt: "2024-07-03T09:15:00Z",
      tags: ["健康", "生活"],
      notebookId: 2,
      type:0,
      isPrivate: false
    },
    {
      username:"方舟孝子",
      noteId: 4,
      title: "工作日志",
      content: "今天啥也没干...",
      createdAt: "2023-05-01T12:00:00Z",
      updatedAt: "2024-07-03T09:15:00Z",
      tags: ["工作","打工"],
      notebookId: 2,
      type:0,
      isPrivate: true
    },
    {
      username:"方舟孝子",
      noteId: 5,
      title: "工作日志5",
      content: "今天啥也没干...",
      createdAt: "2023-05-01T12:00:00Z",
      updatedAt: "2024-07-03T09:15:00Z",
      tags: ["工作","打工"],
      notebookId: 2,
      type:0,
      isPrivate: false
    },
    {
      username:"贴吧吧友",
      noteId: 6,
      title: "笔记6",
      content: JSON.stringify({
        "ur_im": '11111', //紧急且重要
        "nur_im": '2222',//不紧急但重要
        "ur_nim": '3333',//紧急但不重要
        "nur_nim": '4444',//不紧急且不重要
        }),
      createdAt: "2023-05-01T12:00:00Z",
      updatedAt:  "2024-07-01T10:00:00Z",
      tags: ["编程", "Web开发"],
      notebookId: 1,
      type:0,
      isPrivate: true
    }
  ];
  //笔记本
  let notebooks = [
    {
      notebookId: 1,
      name: "编程学习",
      summary: "存放所有与编程相关的笔记",
      lastModified: "2024-07-04T10:30:00Z",
    },
    {
      notebookId: 2,
      name: "生活笔记",
      summary: "记录日常生活中的各种tips",
      lastModified: "2024-07-05T10:30:00Z",
    },
    {
      notebookId: 3,
      name: "工作笔记",
      summary: "记录工作生活中的各种委屈",
      lastModified: "2024-07-06T10:30:00Z",
    },
    {
      notebookId: 4,
      name: "私人笔记",
      summary: "记录隐私东西",
      lastModified: "2024-07-02T10:30:00Z",
    },
    {
      notebookId: 5,
      name: "密码本",
      summary: "存放密码",
      lastModified: "2024-07-06T10:30:00Z",
    },
  ];
  //评论
  let comments = [];
  //点赞数
  let likes = [
    {noteId:2,count:2},
    {noteId:3,count:3},
    {noteId:5,count:5},
  ];

  module.exports = {
    users,
    notebooks,
    notes,
    comments,
    likes,
    // 如果你想要添加一些操作这些数据的函数，也可以在这里导出
    
    // 可以根据需要添加更多的函数
  };