print("hello world");
emitter.setWorkingId("id1");
emitter.emitForWorkingId("key1", "value1");
emitter.setWorkingId("id2");
emitter.emitForWorkingId("key2", "value2");
emitter.emitForWorkingId("key2", "value3");
emitter.flush();