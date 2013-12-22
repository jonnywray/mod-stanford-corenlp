/*
 * Copyright 2013 Jonny Wray
 *
 * @author <a href="http://www.jonnywray.com">Jonny Wray</a>
 */

package com.jonnywray.vertx.corenlp.integration;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

/**
 * Integration tests for the Stanford CoreNLP annotator that constructs a multi-stage pipeline aimed at extracting
 * sentence tokens
 *
 * @author Jonny Wray
 */
public class AnnotatorIT extends TestVerticle {

    @Test
    public void testNonEnglishTest() {
        JsonObject commandObject = new JsonObject();
        commandObject.putString("action", "annotate");
        commandObject.putString("text", "El rápido zorro marrón saltó sobre el perro perezoso");
        vertx.eventBus().send("jonnywray.corenlp", commandObject, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> reply) {
                JsonObject response = reply.body();
                System.out.println(response.encodePrettily());
                assertTrue("Response status is null", response.getString("status") != null);
                assertEquals("Response status is not ok", "ok", response.getString("status"));
                testComplete();
            }
        });
    }

    @Test
    public void testEnglishTest() {
        JsonObject commandObject = new JsonObject();
        commandObject.putString("action", "annotate");
        commandObject.putString("text", "The quick brown fox jumped over the lazy dog");
        vertx.eventBus().send("jonnywray.corenlp", commandObject, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> reply) {
                JsonObject response = reply.body();
                System.out.println(response.encodePrettily());
                assertTrue("Response status is null", response.getString("status") != null);
                assertEquals("Response status is not ok", "ok", response.getString("status"));
                testComplete();
            }
        });
    }

    /**
     * Test response when no text is sent
     */
    @Test
    public void testAnnotateNoText() {
        JsonObject commandObject = new JsonObject();
        commandObject.putString("action", "annotate");
        vertx.eventBus().send("jonnywray.corenlp", commandObject, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> reply) {
                JsonObject response = reply.body();
                assertTrue("Response status is null", response.getString("status") != null);
                assertEquals("Response status is not error", "error", response.getString("status"));
                assertEquals("Response message is not correct", "text to annotate must be specified", response.getString("message"));
                testComplete();
            }
        });
    }


    /**
     * Test response when an invalid action is sent
     */
    @Test
    public void testInvalidAction() {
        JsonObject commandObject = new JsonObject();
        commandObject.putString("action", "invalid");
        vertx.eventBus().send("jonnywray.corenlp", commandObject, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> reply) {
                JsonObject response = reply.body();
                assertTrue("Response status is null", response.getString("status") != null);
                assertEquals("Response status is not error", "error", response.getString("status"));
                assertEquals("Response message is not correct", "unsupported action specified: invalid", response.getString("message"));
                testComplete();
            }
        });
    }


    @Override
    public void start() {
        super.initialize();
        JsonObject configuration = new JsonObject();
        configuration.putString("annotators", "tokenize,ssplit,pos");
        container.deployModule("com.jonnywray.vertx~mod-stanford-corenlp~1.0-SNAPSHOT", configuration, new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> asyncResult) {
                assertTrue(asyncResult.succeeded());
                assertNotNull("deploymentID should not be null", asyncResult.result());
                AnnotatorIT.super.startTests();
            }
        });
    }
}
