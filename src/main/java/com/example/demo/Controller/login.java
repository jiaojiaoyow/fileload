package com.example.demo.Controller;

import com.example.demo.Model.User;
import com.example.demo.Service.UserService;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class login {
    @Autowired
    private UserService userService;

    @RequestMapping("toLogin")
    public ModelAndView toLogin(){
        return new ModelAndView("login");
    }

    @RequestMapping("login")
    public ModelAndView login(User user){
        User user1=userService.selectByPrimaryKey(user.getUsername());
        if(user.getPassword().equals(user1.getPassword())){
            return new ModelAndView("success");
        }
        else {
            return new ModelAndView("fail");
        }
    }

    @RequestMapping(value = "file",method = RequestMethod.POST)
    public void uploadFile( MultipartFile multipartFile)
    {
        //指定存放上传文件的目录
        String fileDir = "D:\\test";
        File dir = new File(fileDir);

        //判断目录是否存在，不存在则创建目录
        if (!dir.exists()){
            dir.mkdirs();
        }

        //生成新文件名，防止文件名重复而导致文件覆盖
        //1、获取原文件后缀名 .img .jpg ....
        String originalFileName = multipartFile.getOriginalFilename();
        String suffix = originalFileName.substring(originalFileName.lastIndexOf('.'));
        //2、使用UUID生成新文件名
        String newFileName = UUID.randomUUID() + suffix;

        //生成文件
        //        C:\ftpfile\img  sdasdasd.jpg
        File file = new File(dir, newFileName);

        //传输内容
        try {
            multipartFile.transferTo(file);
            System.out.println("上传文件成功！");
        } catch (IOException e) {
            System.out.println("上传文件失败！");
            e.printStackTrace();
        }

        //至此，文件已经传到了程序运行的服务器上。
        //下面是这篇博客的重点

        //上传至ftp服务器
        //1、上传文件
        if (uploadToFtp(file)){
            System.out.println("上传至ftp服务器！");
        }else {
            System.out.println("上传至ftp服务器失败!");
        }
        //2、删除本地文件
//        file.delete();
    }

    private boolean uploadToFtp(File file){
        FTPClient ftpClient = new FTPClient();
        try {
            //连接ftp服务器 参数填服务器的ip
            ftpClient.connect("106.52.16.26");

            //进行登录 参数分别为账号 密码
            ftpClient.login("ftpuser","123");

            //改变工作目录（按自己需要是否改变）
            //只能选择local_root下已存在的目录
            ftpClient.changeWorkingDirectory("images");

            //设置文件类型为二进制文件
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            //开启被动模式（按自己如何配置的ftp服务器来决定是否开启）
            //ftpClient.enterLocalPassiveMode();

            //上传文件 参数：上传后的文件名，输入流
            ftpClient.storeFile(file.getName(), new FileInputStream(file));

            ftpClient.disconnect();
            System.out.println(file.getName());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //上传多文件
        @RequestMapping(value = "files",method = RequestMethod.POST)
        public void uploadMoreFile( MultipartFile[] multipartFile)
        {
            //指定存放上传文件的目录
            String fileDir = "D:\\test";
            File dir = new File(fileDir);

            //判断目录是否存在，不存在则创建目录
            if (!dir.exists()){
                dir.mkdirs();
            }

            //生成新文件名，防止文件名重复而导致文件覆盖
            //1、获取原文件后缀名 .img .jpg ....
            for (int i = 0; i <multipartFile.length ; i++) {
                String originalFileName = multipartFile[i].getOriginalFilename();
                String suffix = originalFileName.substring(originalFileName.lastIndexOf('.'));
                //2、使用UUID生成新文件名
                String newFileName = UUID.randomUUID() + suffix;

                //生成文件
                //        C:\ftpfile\img  sdasdasd.jpg
                File file = new File(dir, newFileName);
                //传输内容
                try {
                    multipartFile[i].transferTo(file);
                    System.out.println("上传文件成功！");
                } catch (IOException e) {
                    System.out.println("上传文件失败！");
                    e.printStackTrace();
                }
                //至此，文件已经传到了程序运行的服务器上。
                //下面是这篇博客的重点

                //上传至ftp服务器
                //1、上传文件
                if (uploadToFtp(file)){
                    System.out.println("上传至ftp服务器！");
                }else {
                    System.out.println("上传至ftp服务器失败!");
                }
                //2、删除本地文件
//        file.delete();
            }
    }
}
