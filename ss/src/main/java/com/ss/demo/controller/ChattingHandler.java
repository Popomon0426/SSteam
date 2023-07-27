package com.ss.demo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
@ServerEndpoint (value = "/echo.do")
public class ChattingHandler
{
    // ���� ����Ʈ
    private static final List<Session> sessionList = new ArrayList<Session>();
    // �α�
    private static final Logger logger = LoggerFactory.getLogger(ChattingHandler.class);
    
    // ������
    public ChattingHandler()
    {
        System.out.println("�� ���� ��ü ����");
    }
    
    // �� ���� ����
    @OnOpen
    public void onOpen(Session session)
    {
        logger.info("Open session id:" + session.getId());
        try
        {
            final Basic basic = session.getBasicRemote();
            basic.sendText("ä�ù濡 ���� �Ǿ����ϴ�.");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        sessionList.add(session);
    }
    
    /*
     * ��� �������� �޽����� ����
     * @param self
     * @param sender
     * @param message
     */
    private void sendAllSessionToMessage(Session self, String sender, String message)
    { 	
        try
        {
            for (Session session : ChattingHandler.sessionList)
            {
                if(!self.getId().equals(session.getId()))
                {
                    session.getBasicRemote().sendText(sender + ": " + message);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    /*
     * ���� �Է��ϴ� �޼���
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session)
    {	
    	String sender = message.split(",")[1];
    	message = message.split(",")[0];
    	
        logger.info("Message From " + sender + ": " + message);
        try
        {
            final Basic basic = session.getBasicRemote();
            basic.sendText("<��>: " + message);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        sendAllSessionToMessage(session, sender, message);
    }
    
    @OnError
    public void onError(Throwable e, Session session)
    {
        
    }
    
    // �� ���� �ݱ�
    @OnClose
    public void onClose(Session session)
    {
        logger.info("Session " + session.getId() + " has ended");
        sessionList.remove(session);
    }
}