/*
 * Copyright 2013 Jonny Wray
 *
 * @author <a href="http://www.jonnywray.com">Jonny Wray</a>
 */

package com.jonnywray.vertx.corenlp;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.json.JSONObject;
import org.json.XML;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

/**
 * <p>
 * Verticle acting as an event driven text annotator that uses the <a href="">Stanford CoreNLP</a> library to
 * perform the annotation. The annotator class is pretty lightweight in that is configures a NLP pipeline from
 * a configuration object, allows that pipeline to be called and converts annotated output to JSON.
 * </p>
 * <p>
 *   Since the output of the annotated text depends on the annotators added the JSON returned does not have a fixed
 *   structure. It is converted automatically using the
 *   <a href="http://www.json.org/javadoc/org/json/XML.html#toJSONObject(java.lang.String)">www.json.org utilities</a>
 * </p>
 *
 * @author Jonny Wray
 */
public class Annotator extends BusModBase implements Handler<Message<JsonObject>> {


    private StanfordCoreNLP pipeline;
    private String address;

    @Override
    public void start() {
        super.start();
        address = getOptionalStringConfig("address", "jonnywray.corenlp");
        getMandatoryStringConfig("annotators");
        // assume all configuration parameters are valid. Hard to validate because of potential extensions from the core
        Properties properties = new Properties() ;
        for(String fieldName: config.getFieldNames()){
            String value = config.getString(fieldName);
            properties.put(fieldName, value);
        }
        try{
            pipeline = new StanfordCoreNLP(properties, true);
            eb.registerHandler(address, this);
            container.logger().info("successfully started Stanford CoreNLP module");
        }
        catch (RuntimeException e){
            container.logger().error("error starting Stanford CoreNLP module", e);
            throw e;
        }
    }

    @Override
    public void stop(){
        StanfordCoreNLP.clearAnnotatorPool();
    }

    public void handle(Message<JsonObject> message) {
        String action = message.body().getString("action");
        if (action == null) {
            sendError(message, "action must be specified");
            return;
        }
        switch (action){
            case "annotate":
                annotate(message);
                break;
            default:
                sendError(message, "unsupported action specified: "+action);
        }
    }

    private void annotate(Message<JsonObject> message){
        String text = message.body().getString("text");
        if (text == null) {
            sendError(message, "text to annotate must be specified");
            return;
        }
        try{
            Annotation annotation = pipeline.process(text);
            sendOK(message, convert(annotation));
        }
        catch (IOException e){
            sendError(message, "error processing text: "+e.getMessage());
        }
        catch (RuntimeException e){
            sendError(message, "error processing text: "+e.getMessage());
        }

    }

    private JsonObject convert(Annotation annotation) throws IOException{
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        pipeline.xmlPrint(annotation, writer);
        JSONObject json = XML.toJSONObject(stringWriter.toString());
        return new JsonObject(json.toString());
    }

}
