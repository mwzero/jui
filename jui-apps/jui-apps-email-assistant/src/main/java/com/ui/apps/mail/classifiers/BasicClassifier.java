package com.ui.apps.mail.classifiers;

import java.util.List;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicClassifier {
	
	enum Sentiment {
        POSITIVE, NEUTRAL, NEGATIVE;
    }
	
    interface SentimentAnalyzer {
    	
    	@UserMessage("The sentiment of the text")
    	Sentiment sentiment(String text);
    	
    	@UserMessage("How aggressive the text is on a scale from 1 to 10")
        int aggressive(String text);
    	
    	@UserMessage("The language the text is written in")
        String language(String text);
    	
        @UserMessage("Classifica il testo {{it}} estraendo un elenco di possibili classificazioni")
        String[] labels(String text);
        
        @SystemMessage("Summarize every message from user in {{n}} bullet points. Provide only bullet points.")
        List<String> summarize(@UserMessage String text, @V("n") int n);
        
    }
    
    public static String process(ChatLanguageModel model, String content) {

        SentimentAnalyzer sentimentAnalyzer = AiServices.create(SentimentAnalyzer.class, model);

        Sentiment sentiment = sentimentAnalyzer.sentiment(content);
        int aggressive = sentimentAnalyzer.aggressive(content);
        String language = sentimentAnalyzer.language(content);
        String[] labels = sentimentAnalyzer.labels(content);
        List<String> bulletPoints = sentimentAnalyzer.summarize(content, 3);
        
        log.debug("sentinment is [{}]", sentiment);
        return "";
    }

}
