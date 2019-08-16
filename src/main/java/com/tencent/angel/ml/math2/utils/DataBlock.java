/*
 * Tencent is pleased to support the open source community by making Angel available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/Apache-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.tencent.angel.ml.math2.utils;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;


public abstract class DataBlock<VALUE> {
  private static final Log LOG = LogFactory.getLog(DataBlock.class);

  protected Class<VALUE> valueClass;
  protected volatile int readIndex;
  protected volatile int writeIndex;

  public synchronized void incrReadIndex() {
    readIndex++;
  }

  public synchronized void decrReadIndex() {
    readIndex--;
  }

  public synchronized void incrWriteIndex() {
    writeIndex++;
  }

  public synchronized void decrWriteIndex() {
    writeIndex--;
  }

  public DataBlock() {
    readIndex = 0;
    writeIndex = 0;
  }

  public void registerType(Class<VALUE> valueClass) {
    this.valueClass = valueClass;
  }

  public abstract VALUE read() throws IOException;

  protected abstract boolean hasNext() throws IOException;

  public abstract VALUE get(int index) throws IOException;

  public abstract void put(VALUE value) throws IOException;

  public abstract void resetReadIndex() throws IOException;

  public abstract void clean() throws IOException;

  public abstract void shuffle() throws IOException;

  public abstract void flush() throws IOException;

  public abstract DataBlock<VALUE> slice(int startIndex, int length) throws IOException;

  public Class<VALUE> getValueClass() {
    return valueClass;
  }

  public void setValueClass(Class<VALUE> valueClass) {
    this.valueClass = valueClass;
  }

  public int getReadIndex() {
    return readIndex;
  }

  public void setReadIndex(int readIndex) {
    this.readIndex = readIndex;
  }

  public int getWriteIndex() {
    return writeIndex;
  }

  public void setWriteIndex(int writeIndex) {
    this.writeIndex = writeIndex;
  }

  public int size() {
    return writeIndex;
  }

  public float getProgress() {
    if (writeIndex == 0) {
      return 0.0f;
    }
    return (float) readIndex / writeIndex;
  }

  public VALUE loopingRead() throws Exception {
    VALUE data = this.read();
    if (data == null) {
      resetReadIndex();
      data = read();
    }

    if (data != null)
      return data;
    else
      throw new Exception("Train data storage is empty or corrupted.");
  }

  @Override public String toString() {
    return "DataBlock [valueClass=" + valueClass + ", readIndex=" + readIndex + ", writeIndex="
      + writeIndex + "]";
  }
}