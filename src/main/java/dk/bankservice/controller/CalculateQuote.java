/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.bankservice.controller;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import dk.bankservice.dto.LoanRequestDTO;
import dk.bankservice.dto.LoanResponseDTO;
import dk.bankservice.messaging.Send;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author ptoma
 */
public class CalculateQuote 
{
    private static Gson gson;
    
    public static void calculateInterest(String request, String properties) throws IOException, TimeoutException
    {
        gson = new Gson();
        
        AMQP.BasicProperties props = gson.fromJson(properties, AMQP.BasicProperties.class);
        
        System.out.println(props.toString());
        
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder().correlationId(props.getCorrelationId()).replyTo(props.getReplyTo()).build();

        double interestRate = new Random().nextDouble()*20;
        
        LoanRequestDTO loanRequestDTO = gson.fromJson(request, LoanRequestDTO.class);

        LoanResponseDTO loanResponseDTO = new LoanResponseDTO(interestRate, loanRequestDTO.getSsn());

        sendMessage(loanResponseDTO,replyProps);
    }
    
    public static void sendMessage(LoanResponseDTO dto, AMQP.BasicProperties props) throws IOException, TimeoutException 
    {
        String message = gson.toJson(dto);
        
        Send.sendMessage(message,props);
    }
}
