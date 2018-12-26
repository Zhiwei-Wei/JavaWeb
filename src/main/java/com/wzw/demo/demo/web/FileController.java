package com.wzw.demo.demo.web;

import com.wzw.demo.demo.repo.FileRepository;
import com.wzw.demo.demo.repo.UserRepository;
import com.wzw.demo.demo.util.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

@Controller
public class FileController {
    @Autowired
    FileRepository fileRepository;
    @Autowired
    UserRepository userRepository;
    @RequestMapping(value = "/course/{courseId}/question/{questionId}/postImg", method = RequestMethod.POST)
    @ResponseBody
    public String handleFileUpload(@PathVariable Integer courseId, @PathVariable
            Integer questionId, @RequestParam(value = "fileList") MultipartFile fileList,
                                   @RequestParam(value = "type") String type,
                                   HttpServletRequest request) throws IOException {
        if(userRepository.isBannedPost((Integer) request.getSession().getAttribute("uid"),courseId)){
            return "";
        }
        Integer answerId = (Integer) request.getSession().getAttribute("answerId");
        Integer order = (Integer) request.getSession().getAttribute("order");
        request.getSession().setAttribute("order",order+1);
        System.out.println("开始上传第"+order+"个文件！");
        fileRepository.addFile(fileList,type,questionId,answerId,order);

        System.out.println("成功上传文件！");
        return "upload successful";
    }

    @RequestMapping(value = "/course/{courseId}/postImg", method = RequestMethod.POST)
    @ResponseBody
    public String handleFileUploadQ(@PathVariable Integer courseId,  @RequestParam(value = "fileList") MultipartFile fileList,
                                   @RequestParam(value = "type") String type,
                                   HttpServletRequest request) throws IOException {
        Integer questionId = (Integer) request.getSession().getAttribute("questionId");
        Integer order = (Integer) request.getSession().getAttribute("order");
        request.getSession().setAttribute("order",order+1);
        System.out.println("开始上传问题的第"+order+"个文件！");
        fileRepository.addFile(fileList,type,questionId,0,order);
        System.out.println("成功上传文件！");
        return "upload successful";
    }
    @RequestMapping(value = "/course/download/{qId}/{aId}/{order}")
    public void getImg(HttpServletResponse response,
                       @PathVariable Integer qId,@PathVariable Integer aId,
                       @PathVariable Integer order) throws IOException {
        BufferedImage image;
        image = ImageIO.read(fileRepository.getPictures(qId,aId,order));
        //Graphics g = image.getGraphics();
        //加了下面这一行会在原图片上显示这个大小的700 600 大小的图片
        //g.drawImage(image,0,0,700,600,null);
        ImageIO.write(image, "JPEG", response.getOutputStream());
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

}
