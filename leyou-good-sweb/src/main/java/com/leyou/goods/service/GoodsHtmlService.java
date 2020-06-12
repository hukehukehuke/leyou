package com.leyou.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;

@Service
public class GoodsHtmlService {

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private GoodsService goodsService;
    public  void createHtml(Long spudId){
        //初始化运行上下文
        Context context = new Context();
        PrintWriter printWriter = null;
        context.setVariables(this.goodsService.loadData(spudId));
       try{
           File file = new File("Nginx的目录位置" + spudId + ".html");
           printWriter = new PrintWriter(file);
           this.templateEngine.process("item",context,printWriter);
       }catch (Exception e){
           e.printStackTrace();
       }finally {
           if(printWriter != null){
               printWriter.close();
           }
       }
    }
}
