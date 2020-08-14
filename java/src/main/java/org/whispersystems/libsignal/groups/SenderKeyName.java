/**
 * Copyright (C) 2014-2016 Open Whisper Systems
 *
 * Licensed according to the LICENSE file in this repository.
 */
package org.whispersystems.libsignal.groups;

import org.whispersystems.libsignal.SignalProtocolAddress;

/**
 * A representation of a (groupId + senderId + deviceId) tuple.
 */
public class SenderKeyName {

  private static native long New(String groupid, String senderName, int senderDeviceId);
  private static native void Destroy(long handle);
  private static native String GetSenderName(long handle);
  private static native int GetSenderDeviceId(long handle);
  private static native String GetGroupId(long handle);

  static {
       System.loadLibrary("signal_jni");
  }

  private long handle;

  public SenderKeyName(String groupId, SignalProtocolAddress sender) {
    this.handle = New(groupId, sender.getName(), sender.getDeviceId());
  }

  public SenderKeyName(String groupId, String senderName, int senderDeviceId) {
    this.handle = New(groupId, senderName, senderDeviceId);
  }

  @Override
  protected void finalize() {
    Destroy(this.handle);
  }

  public String getGroupId() {
    return GetGroupId(this.handle);
  }

  public SignalProtocolAddress getSender() {
    return new SignalProtocolAddress(GetSenderName(this.handle), GetSenderDeviceId(this.handle));
  }

  public String serialize() {
    SignalProtocolAddress sender = this.getSender();
    return this.getGroupId() + "::" + sender.getName() + "::" + String.valueOf(sender.getDeviceId());
  }

  @Override
  public boolean equals(Object other) {
    if (other == null)                     return false;
    if (!(other instanceof SenderKeyName)) return false;

    SenderKeyName that = (SenderKeyName)other;

    return
       this.getGroupId().equals(that.getGroupId()) &&
       this.getSender().equals(that.getSender());
  }

  @Override
  public int hashCode() {
    return this.serialize().hashCode();
  }

  public long nativeHandle() {
    return this.handle;
  }

}
