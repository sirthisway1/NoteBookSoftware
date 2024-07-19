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
                <div class="keyWords" id="pie"></div>
                    <!-- <h3>本周最常用关键词</h3>
                    <ul>
                        <li v-for="(word, index) in topKeywords" :key="index">{{ word }}</li>
                    </ul> -->
                
                <div class="countTag" id="bar"></div>
                    <!-- <h3>最多使用的标签</h3>
                    <ul>
                        <li v-for="(tag, index) in topTags" :key="index">{{ tag }}</li>
                    </ul> -->
                
                <div class="timeMessage" >
                    <h3>本周关键词</h3>
                    <p v-if="timeActivity.updatedAt && timeActivity.title">
                        一天里，最晚在 {{ formattedTime }}，你还在写 {{ timeActivity.title }} ，这一周看来你很关心:
                    </p>
                    <p v-else>暂无时间信息</p>
                    <div id="wordcloud" class="wordCloudBox" style="width: 100%; height: 400px;"></div>
                   
                    
                </div>
            </div>
            
        </div> 
    </div>
</template>


<script>
import axios from 'axios';
import * as echarts from 'echarts';
import { ElMessage } from 'element-plus';
import "echarts-wordcloud";//引入词云

export default {
    data() {
        return {
        useravatar:'',
        username: '',
        lineData: [150, 230, 224, 218, 135, 147, 260],
        pieDate:[
        { value: 400, name: 'rose 1' },
        { value: 380, name: 'rose 2' },
        { value: 320, name: 'rose 3' },
        { value: 300, name: 'rose 4' },
        { value: 280, name: 'rose 5' },
        { value: 260, name: 'rose 6' },
        { value: 220, name: 'rose 7' },
        { value: 180, name: 'rose 8' }
         ],
        // topKeywords: ["元素","神魔","天才","显示","游戏"],
        barDate:[120, 200, 150, 80, 70],
        topTags: ["元素","神魔","天才","显示","游戏"],
        times:['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日'],
        timeActivity: { updatedAt: '7月20号凌晨五点半', title: '启动元神',content:''},
        keywords:[],
        WordCloudData: [
	        {
	          name: "货运",
	          value:1,
	        },
	        {
	          name: "管理",
	          value: 2,
	        },
	        {
	          name: "信息化",
	          value:3,
	        },
	      ],
        }
    },
    computed: {
        formattedTime() {
            return this.timeActivity.updatedAt.split(' ')[1];
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
          const token = localStorage.getItem('token');
          try {
            const response = await axios.get('/api/notes/noteFetchActivity',{headers: { token: token }});
            if (response.data && response.data.code === "200") {
              this.times = response.data.data.dates
              this.lineData = response.data.data.activities;
              this.updateChartLine();
            }
          } catch (error) {
            console.error("Error fetching chart data:", error);
          }
        },
        async fetchTopKeywords() {
            const token = localStorage.getItem('token');
            try {
                const response = await axios.get('/api/notes/top-keywords',{headers: { token: token }});
                if (response.data && response.data.code === "200") {
                
                this.WordCloudData=response.data.data;
                this.updateChartPie();
                this.getWordCloud();
                }
            } catch (error) {
                console.error("Error fetching top keywords:", error);
            }
        },
        async fetchTopTags() {
            const token = localStorage.getItem('token');
            try {
                const response = await axios.get('/api/notes/noteTopTags',{headers: { token: token }});
                if (response.data && response.data.code === "200") {
                this.topTags = response.data.data.tags;
                this.barDate = response.data.data.counts;
                this.updateChartBar();
                }
            } catch (error) {
                console.error("Error fetching top tags:", error);
            }
        },
        updateChartLine() {
            var lineChartDom = document.getElementById('line');
            var lineChart = echarts.init(lineChartDom);
            
            const lineoption = {
                title: {
                    left: 'center',
                    text: '本周修改编辑笔记分布' //柱状图标题
                },
                tooltip:{
                    trigger: 'axis'
                },
                xAxis: {
                    name:"时间",
                    type: 'category',
                    data: this.times,
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
                        data: this.lineData,
                        type: 'line'
                    }
                ]
            };
            
            lineChart.setOption(lineoption);
        },
        updateChartPie() {
            var pieChartDom = document.getElementById('pie');
            var pieChart = echarts.init(pieChartDom);
            
            const pieoption = {
                title: {
                    left: 'center',
                    text: '本周笔记字数统计' //柱状图标题
                },
                tooltip:{
                    trigger: 'item'
                },
                legend: {
                    top: 'bottom'
                },
                toolbox: {
                    show: true,
                    feature: {
                    mark: { show: true },
                    dataView: { show: true, readOnly: false },
                    restore: { show: false },
                    saveAsImage: { show: true }
                    }
                },
                series: [
                    {
                    name: 'Nightingale Chart',
                    type: 'pie',
                    radius: [30, 200],
                    center: ['50%', '50%'],
                    roseType: 'area',
                    itemStyle: {
                        borderRadius: 3
                    },
                    data: this.pieDate
                    }
                ]
            };
            
            pieChart.setOption(pieoption);
        },
        updateChartBar() {
            var barChartDom = document.getElementById('bar');
            var barChart = echarts.init(barChartDom);
            
            const baroption = {
                tooltip:{
                    trigger: 'axis'
                },
                title: {
                    left: 'center',
                    text: '本周标签' //柱状图标题
                },
                xAxis: {
                    type: 'category',
                    data: this.topTags,
                },
                yAxis: {
                    type: 'value'
                },
                series: [
                    {
                    data: this.barDate,
                    type: 'bar'
                    }
                ]
            };
            
            barChart.setOption(baroption);
        },
        getWordCloud() {
          try {var accessToElements = document.getElementById("wordcloud"); //绑定元素
	      var themeStyle = echarts.init(accessToElements); 
	      //图表自适应配置
	      const chartNode = new ResizeObserver(() => {
	        themeStyle.resize();
	      });
	      chartNode.observe(accessToElements);
	      // 绘制图表
	      const option = {
	        series: [
              {
                type: "wordCloud",
    
                //要绘制的“云”的形状。任意极坐标方程都可以表示为a吗
                //回调函数，或关键字存在。可用的礼物为圆形(默认)，
                //心形(苹果或心形曲线，最著名的极坐标方程)，菱形(
                // alias of square)， triangle-forward, triangle， (alias of triangle- standing, pentagon, and star)
    
                shape: "star , triangle",
    
                //保持maskImage或1:1的形状的纵横比
                // echarts-wordcloud@2.1.0支持该选项
                keepAspect: false,
    
                //一个轮廓图像，白色区域将被排除在绘制文本之外。
                //当云的形状增长时，形状选项将继续应用。
                // maskImage: maskImage,
    
                //跟随左/顶/宽/高/右/底是用来定位字云
                //默认放置在中心，大小为75% x 80%。
    
                left: "center",
                top: "center",
                width: "80%",
                height: "80%",
                right: null,
                bottom: null,
    
                //文本大小范围，数据中的值将被映射到。
                //默认最小12px，最大60px大小。
    
                sizeRange: [20, 40],
    
                //文本旋转范围和步进度。文本将被rotationStep 45在[- 90,90]范围内随机旋转
    
                rotationRange: [-45, 90],
                rotationStep: 45,
    
                //网格的大小(以像素为单位)，用于标记画布的可用性
                //网格大小越大，单词之间的间距越大。
    
                gridSize: 8,
    
                //设置为true允许文字部分绘制在画布外。
                //允许文字大于画布的大小
                drawOutOfBound: false,
    
                //如果执行布局动画。
                //注意当有很多字的时候禁用它会导致UI阻塞。
                layoutAnimation: true,
    
                // Global text style
                textStyle: {
                  fontFamily: "sans-serif",
                  fontWeight: "bold",
                  // Color can be a callback function or a color string
                  color: function () {
                    // Random color
                    return (
                      "rgb(" +
                      [
                        Math.round(Math.random() * 160),
                        Math.round(Math.random() * 160),
                        Math.round(Math.random() * 160),
                      ].join(",") +
                      ")"
                    );
                  },
                },
                emphasis: {
                  focus: "self",
                },
    
                // 内容，
                data: this.WordCloudData,
              },
            ],
          };
            themeStyle.setOption(option);
          } catch (error) {
            console.error('词云渲染错误:', error);
          }
	      
	      },
        async fetchTimeActivity() {
            const token = localStorage.getItem('token');
            try {
            const response = await axios.get('/api/notes/noteFetchTime', {
                headers: { token: token }
            });
            if (response.data && response.data.code === "200") {
                this.timeActivity = response.data.data;
            }
            } catch (error) {
            console.error("Error fetching time activity:", error);
            }
        },
        
        async fetchWordsCount() {
            const token = localStorage.getItem('token');
            try {
            const response = await axios.get('/api/notes/notesCountWords', {
                headers: { token: token }
            });
            if (response.data && response.data.code === "200") {
                this.pieDate = response.data.data;
                this.updateChartPie();
            }
            } catch (error) {
            console.error("Error Words Count:", error);
            }
        },

    },
    mounted() {
        this.fetchCurrentUser();
        this.updateChartLine();
        this.fetchChartData();
        // this.fetchTopKeywords();
        this.fetchTopTags();
        this.fetchTimeActivity();
        this.updateChartPie();
        this.updateChartBar();
        this.fetchWordsCount();
        this.$nextTick(() => {
          this.getWordCloud();
        });
        
    }
}
</script>


<style scoped>
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
  border: 4px solid #409bf6;
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
    height: 500px;
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
    height: 500px;
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
.bestsellers-container {
    height: 18.56rem;
    background: #f0f0f0;
 
    #charts-content {
      /* 需要设置宽高后才会显示 */
      width: 100%;
      height: 100%;
    }
  }
  .wordCloudBox{
	height: 300px;
}
</style>
