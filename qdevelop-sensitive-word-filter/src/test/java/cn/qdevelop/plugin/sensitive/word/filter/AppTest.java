package cn.qdevelop.plugin.sensitive.word.filter;

import cn.qdevelop.plugin.sensitiveword.impl.KeyWordFinder;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	String text = "创业需要了解什么 [创业需要了解什么]为什么中国人现在更需要创业？-- 创办企业的传统，而几十年的计划经济更是窒息了中国人的创业精神。相反，中国人在海外却涌现出了大批华商。如今创业精神开始在中国被发扬光大，创业大潮开始掀起新的高潮 最新文章 [w与v发动机的比较]谁更平顺？缸发动机与缸机震动比拼 [中秋节为什么团圆]中秋随想（一）团圆节 [汽车音乐节]汽车音乐节招商案 [阳朔旅游]阳朔之旅 [古今交友的名言名句]古今交友的名言佳句 [中国企业海外并购]国外酒店并购 [世界人权日]写在纪念世界人权日之际 [独生子女父母奖励费]关于发放独生子女父母奖励费的通知 [乌镇旅游游记攻略]寒冬腊月游乌镇_磨迹旅游网 [陆羽与茶经ppt]《_陆羽与茶经》PPT [国庆周年]国庆周年诗歌（二） [我的爱永远陪着你]致永远爱着的WXY——写于长沙归来 热门文章 [五行属土的字大全]五行属土的汉字大全 [老公每天晚上都要吃奶]老公每晚都要吃奶睡觉都要含着睡 [在深圳合租的日子完整]我在深圳做鸭的日子（完） [我和我妈妈的真实经历]我的妈妈 [第一次性经历]女大学生自述第一次性经历 [中国车牌号的识别大全]中国车牌的识别(绝对有用） [轮渡时刻表]上海全部轮渡时刻表汇总 [验孕棒一深一浅图解]验孕棒一深一浅的原因 [打耳洞后的注意事项]打耳洞的注意事项 [徐娘半老风韵犹存]半老徐娘性感撩人，风韵犹存 [励志歌曲排行榜首]二十首最棒的励志歌曲 [哔哩哔哩答题全部答案]最新哔哩哔哩会员注册答题技巧及答案 优秀文章 [洛可可设计贾伟老婆]洛可可设计创始人贾伟 [神探狄仁杰部全集]神探狄仁杰百度网盘下载 电视剧神探狄仁杰全集下载百度云盘地址 [女性生理结构]你了解自己的身体结构吗？揭秘女性生理结构的秘密。 [军长]史上最牛军长 [上海甜心丝足恋足会所]恋足者大聚会(编发) [营业执照年检网上申报]营业执照年检网上申报步骤 [雨刷尺寸对照表]雨刮器雨刷尺寸车型对照表 [名家名篇经典美文摘抄]篇必读名家经典美文 [爱新觉罗溥仪后人]溥仪去世，为什么爱新觉罗家族的人不来送葬 [中国香烟排行榜]中国香烟排行榜（附图） [疼到撕心裂肺的句子]哭到撕心裂肺的经典爱情句子 [墨宝非宝至此终年番外]墨宝非宝---番外 中国";
    	String ss = KeyWordFinder.getInstance().findKeyWord(text);
    	
    	System.out.println(text.replaceAll(ss, "***"));
    }
}
