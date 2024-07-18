<template>
    <div class="container">
        <div class="sidebar">
            <div class="sidebar-user">
            <img :src="useravatar || 'default-avatar.png'" alt="User Avatar" class="sidebar-avatar">
            <div class="sidebar-username" id="username">{{ username }}</div>
            </div>
            <div class="sidebar-item active" @click="goToStart">
            <el-icon :size="40"><HomeFilled /></el-icon>
            开始
            </div>
            <div class="sidebar-item" @click="goToNotebook">笔记本</div>
            <div class="sidebar-item" @click="goToCommunity">发现社区</div>
            <div class="sidebar-item" @click="goToUserCenter">用户中心</div>
        </div>
         <div class="main-content">
            <div class="content-wrapper">
                <div class="Echarts" id="line"></div>
                <div class="keyWords">
                    <h3>本周最常用关键词</h3>
                    <ul>
                        <li v-for="(word, index) in topKeywords" :key="index">{{ word }}</li>
                    </ul>
                </div>
                <div class="countTag">
                    <h3>最多使用的标签</h3>
                    <ul>
                        <li v-for="(tag, index) in topTags" :key="index">{{ tag }}</li>
                    </ul>
                </div>
                <div class="timeMessage">
                    <h3>时间信息</h3>
                    <p v-if="timeActivity.time && timeActivity.activity">
                        在 {{ timeActivity.time }}，你还在 {{ timeActivity.activity }}
                    </p>
                    <p v-else>暂无时间信息</p>
                </div>
            </div>
            
        </div> 
    </div>
</template>


<script>
import axios from 'axios';
import * as echarts from 'echarts';

export default {
    data() {
        return {
        useravatar:'',
        username: '',
        chartData: [150, 230, 224, 218, 135, 147, 260],
        topKeywords: ["元素","神魔"],
        topTags: ["牛魔","傻逼"],
        timeActivity: { time: '7月20号凌晨五点半', activity: '启动元神' }
        }
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
        this.$router.push({ name: 'UserCenter' });
        },
         //获取当前用户
        async fetchCurrentUser() {
        const token = localStorage.getItem('token');
            if (!token) {
            alert('请先登录');
            this.$router.push('/login');
            return;
            }

        try {
            const response = await axios.get('/api/user',{headers: { token: token }});
            if (response.data && response.data.code === "200") {
            this.currentUser = response.data.data;
            this.username = this.currentUser.username;
            this.useravatar = this.currentUser.avatar;
            console.log('用户名：',this.username);
            }
        } catch (error) {
            console.error("Error fetching current user:", error);
        }
        },
        async fetchChartData() {
        //   try {
        //     const response = await axios.get('/api/weekly-chart-data');
        //     if (response.data && response.data.code === "200") {
        //       this.chartData = response.data.data;
        //       this.updateChart();
        //     }
        //   } catch (error) {
        //     console.error("Error fetching chart data:", error);
        //   }
        },
        async fetchTopKeywords() {
            // try {
            //     const response = await axios.get('/api/top-keywords');
            //     if (response.data && response.data.code === "200") {
            //     this.topKeywords = response.data.data;
            //     }
            // } catch (error) {
            //     console.error("Error fetching top keywords:", error);
            // }
        },
        async fetchTopTags() {
            // const token = localStorage.getItem('token');
            // try {
            //     const response = await axios.get('/api/notes/noteTopTags',,{headers: { token: token }});
            //     if (response.data && response.data.code === "200") {
            //     this.topTags = response.data.data.topTags;
            //     }
            // } catch (error) {
            //     console.error("Error fetching top tags:", error);
            // }
        },
        async fetchTimeActivity() {
            // const token = localStorage.getItem('token');
            // try {
            // const response = await axios.get('/api/user-time-activity', {
            //     headers: { token: token }
            // });
            // if (response.data && response.data.code === "200") {
            //     this.timeActivity = response.data.data;
            // }
            // } catch (error) {
            // console.error("Error fetching time activity:", error);
            // }
        },
        updateChart() {
            var chartDom = document.getElementById('line');
            var myChart = echarts.init(chartDom);
            
            const option = {
                title: {
                    left: 'center',
                    text: '本周修改编辑笔记次数' //柱状图标题
                },
                tooltip:{
                    trigger: 'axis'
                },
                xAxis: {
                    name:"时间",
                    type: 'category',
                    data: ['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日'],
                    axisLabel:{
                        color: '#409bf6'
                    },
                    splitLine:{
                        show: true
                    }
                },
                yAxis: {
                    name:"修改次数",//y轴标题
                    type: 'value'
                },
                series: [
                    {
                        data: this.chartData,
                        type: 'line'
                    }
                ]
            };
            
            myChart.setOption(option);
        }

    },
    mounted() {
        this.fetchCurrentUser();
        this.updateChart();
        this.fetchChartData();
        this.fetchTopKeywords();
        this.fetchTopTags();
        this.fetchTimeActivity();
    }
}
</script>


<style>
.container {
  display: flex;
  height: 100vh;
  width: 100%;
  position: fixed;
  top: 0;
  left: 0;
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
  color: #6e6e6e;
  transition: background-color 0.3s;
}

.sidebar-item:hover {
  background-color: #f0f0f0;
}

.sidebar-item.active{
  background-color: #409bf6;
  color: white;
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
.content-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px; /* 为各个部分之间添加间距 */
}
.Echarts{
    height: 400px;
    width: 800px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    padding: 40px;
}
.keyWords{
    height: 300px;
    width: 800px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    padding: 40px;
}
.countTag{
    height: 300px;
    width: 800px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    padding: 40px;
}
.timeMessage{
    height: 300px;
    width: 800px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    padding: 40px;
}
</style>