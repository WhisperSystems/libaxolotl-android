/**
 * Copyright (C) 2014-2016 Open Whisper Systems
 *
 * Licensed according to the LICENSE file in this repository.
 */
package org.whispersystems.libsignal.groups;

import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.InvalidKeyIdException;
import org.whispersystems.libsignal.groups.state.SenderKeyRecord;
import org.whispersystems.libsignal.groups.state.SenderKeyState;
import org.whispersystems.libsignal.groups.state.SenderKeyStore;
import org.whispersystems.libsignal.protocol.SenderKeyDistributionMessage;
import org.whispersystems.libsignal.util.KeyHelper;

/**
 * GroupSessionBuilder is responsible for setting up group SenderKey encrypted sessions.
 *
 * Once a session has been established, {@link org.whispersystems.libsignal.groups.GroupCipher}
 * can be used to encrypt/decrypt messages in that session.
 * <p>
 * The built sessions are unidirectional: they can be used either for sending or for receiving,
 * but not both.
 *
 * Sessions are constructed per (groupId + senderId + deviceId) tuple.  Remote logical users
 * are identified by their senderId, and each logical recipientId can have multiple physical
 * devices.
 *
 * @author Moxie Marlinspike
 */

public class GroupSessionBuilder {

  private static native long CreateSenderKeyDistributionMessage(long senderKeyNameHandle,
                                                                SenderKeyStore senderKeyStore);
  private static native void ProcessSenderKeyDistributionMessage(long senderKeyNameHandle,
                                                                 long senderKeyDistributionMessageHandle,
                                                                 SenderKeyStore senderKeyStore);

  private final SenderKeyStore senderKeyStore;

  public GroupSessionBuilder(SenderKeyStore senderKeyStore) {
    this.senderKeyStore = senderKeyStore;
  }

  /**
   * Construct a group session for receiving messages from senderKeyName.
   *
   * @param senderKeyName The (groupId, senderId, deviceId) tuple associated with the SenderKeyDistributionMessage.
   * @param senderKeyDistributionMessage A received SenderKeyDistributionMessage.
   */
  public void process(SenderKeyName senderKeyName, SenderKeyDistributionMessage senderKeyDistributionMessage) {
    synchronized (GroupCipher.LOCK) {
       ProcessSenderKeyDistributionMessage(senderKeyName.nativeHandle(),
                                           senderKeyDistributionMessage.nativeHandle(),
                                           senderKeyStore);
    }
  }

  /**
   * Construct a group session for sending messages.
   *
   * @param senderKeyName The (groupId, senderId, deviceId) tuple.  In this case, 'senderId' should be the caller.
   * @return A SenderKeyDistributionMessage that is individually distributed to each member of the group.
   */
  public SenderKeyDistributionMessage create(SenderKeyName senderKeyName) {
    synchronized (GroupCipher.LOCK) {
      return new SenderKeyDistributionMessage(CreateSenderKeyDistributionMessage(senderKeyName.nativeHandle(), senderKeyStore));
    }
  }
}
