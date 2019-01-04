package com.stylefeng.guns.core.util;

//实现令牌桶
public class TokenBucket {

    private int bucketNums=100; //桶的容量
    private int rate=1; //流入速度
    private int nowTokens; //当前令牌数量

    private long timestamp=getNowTime();

    private long getNowTime() {
        return System.currentTimeMillis();
    }

    private int min(int tokens){
        return bucketNums>tokens?tokens:bucketNums;
    }

    public boolean getToken(){
        //记录来拿令牌的时间
        long nowTime=getNowTime();
        //添加令牌【判断该有多少个令牌】
        nowTokens=nowTokens+(int)((nowTime-timestamp)*rate);
        //添加以后的令牌数量与桶的容量哪个小
        nowTokens=min(nowTokens);
        //修改拿令牌的时间
        timestamp=nowTime;
        //判断是否足够
        if (nowTokens>=1){
            nowTokens-=1;
            return true;
        }else {
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TokenBucket tokenBucket=new TokenBucket();
        for(int i=0;i<200;i++){
            if (i==0){
                Thread.sleep(500);
            }
            System.out.println("第"+i+"次请求结果="+tokenBucket.getToken());
        }
    }

}
