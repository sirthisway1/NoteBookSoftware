// ChartWordCloud.vue
 
<template>
	<!-- 词云图 -->
	<div id="wordCloud" class="wordCloudBox"></div>
</template>

 
<script>
import * as echarts from 'echarts';
import "echarts-wordcloud";//引入词云
export default {
 data() {
    return {
       //词云数据格式
	   WordCloudData: [
	        {
	          name: "货运",
	          value: 1446,
	        },
	        {
	          name: "管理",
	          value: 928,
	        },
	        {
	          name: "信息化",
	          value: 928,
	        },
	      ],
		}
    },
     mounted() {
		this.getWordCloud();
	},
	 methods: {
		 // 词云图
	    getWordCloud() {
	      var accessToElements = document.getElementById("wordCloud"); //绑定元素
	      var themeStyle = echarts.init(accessToElements); 
	      //图表自适应配置
	      const chartNode = new ResizeObserver(() => {
	        themeStyle.resize();
	      });
	      chartNode.observe(accessToElements);
	      // 绘制图表
	      var option = {
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
	      option && themeStyle.setOption(option);
	    },
	}
}
</script>

 
<style scoped>
.wordCloudBox{
	height: 300px;
}
</style>