package com.suggestion.web;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by yjj on 16/2/3.
 */
@Log4j2
public class Main {


    @Setter
    @Getter
    static class A{

        List<Integer> list = Lists.newArrayList();

    }


    public static void main(String[] args) throws InterruptedException {


        List<Integer> list1 = Lists.newArrayList(1);

        A a = new A();
        a.setList(list1);
        List<Integer> tmp = a.getList();
        List<Integer> list2= Lists.newArrayList(2);
        a.setList(list2);
        
        System.out.println(Joiner.on(" ").join(tmp));

    }
}
