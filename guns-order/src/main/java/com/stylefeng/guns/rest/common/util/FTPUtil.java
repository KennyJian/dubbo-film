package com.stylefeng.guns.rest.common.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.*;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FTPUtil {

    //地址 端口 用户名 密码
    private String hostName;
    private Integer port;
    private String userName;
    private String password;

    private FTPClient ftpClient=null;

    private void initFTPClient(){
        try {
            ftpClient=new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            ftpClient.connect(hostName,port);
            ftpClient.login(userName,password);
        }catch (Exception e){
            log.error("初始化FTP失败",e);
        }
    }

    //输入一个路径,然后将路径里的文件转换成字符串返回给我
    public String getFileStrByAddress(String fileAddress) throws IOException {
        BufferedReader bufferedReader=null;
        try {
            initFTPClient();
            FTPFile[] ftpFiles = ftpClient.listDirectories();
            for (FTPFile ftpFile:ftpFiles){
                System.out.println(ftpFile.getName());
            }
//            bufferedReader=new BufferedReader(new InputStreamReader(ftpClient.retrieveFileStream("pub/temp/"+fileAddress)));
//            linux写死
//            bufferedReader=new BufferedReader(new InputStreamReader(new FileInputStream(new File("/home/kenny/eache/cgs.json"))));
            //windows写死
            bufferedReader=new BufferedReader(new InputStreamReader(new FileInputStream(new File("E:\\cgs.json"))));
            StringBuffer stringBuffer=new StringBuffer();
            while (true){
                String lineStr=bufferedReader.readLine();
                if (lineStr==null){
                    break;
                }
                stringBuffer.append(lineStr);
            }
            ftpClient.logout();
            return stringBuffer.toString();
        }catch (Exception e){
             log.error("获取FTP文件信息失败",e);
        }finally {
            if (bufferedReader!=null){
                bufferedReader.close();  //当时是注释的
            }
//            return stringBuffer.toString();
        }
        return null;
    }

    public static void main(String[] args){
        FTPUtil ftpUtil=new FTPUtil();
        try {
            String fileStrByAddress=ftpUtil.getFileStrByAddress("seats.json");
            System.out.println(fileStrByAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
