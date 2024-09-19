package com.ui.apps.mail.classifiers;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SentimentClassifier {
	
	enum Sentiment {
        POSITIVE, NEUTRAL, NEGATIVE;
    }

    interface SentimentAnalyzer {

        @UserMessage("Analyze sentiment of {{it}}")
        Sentiment analyzeSentimentOf(String text);

        @UserMessage("Does {{it}} have a positive sentiment?")
        boolean isPositive(String text);
        
        
    }
    
    public Sentiment process(ChatLanguageModel model, String content) {

        SentimentAnalyzer sentimentAnalyzer = AiServices.create(SentimentAnalyzer.class, model);

        Sentiment sentiment = sentimentAnalyzer.analyzeSentimentOf(content);
        log.debug("sentinment is [{}]", sentiment);
        return sentiment;
    }

}
