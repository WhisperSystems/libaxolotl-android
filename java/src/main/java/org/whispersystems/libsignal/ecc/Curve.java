/**
 * Copyright (C) 2013-2016 Open Whisper Systems
 *
 * Licensed according to the LICENSE file in this repository.
 */
package org.whispersystems.libsignal.ecc;

import org.whispersystems.curve25519.Curve25519;
import org.whispersystems.curve25519.Curve25519KeyPair;
import org.whispersystems.libsignal.InvalidKeyException;

import static org.whispersystems.curve25519.Curve25519.BEST;

import org.whispersystems.libsignal.util.ByteUtil;

public class Curve {

  public  static final int DJB_TYPE   = 0x05;

  public static boolean isNative() {
    return false;
  }

  public static ECKeyPair generateKeyPair() {
    Curve25519KeyPair keyPair = Curve25519.getInstance(BEST).generateKeyPair();

    byte[] type = {Curve.DJB_TYPE};
    byte[] pubkey = ByteUtil.combine(type, keyPair.getPublicKey());

    return new ECKeyPair(new ECPublicKey(pubkey),
                         new ECPrivateKey(keyPair.getPrivateKey()));
  }

  public static ECPublicKey decodePoint(byte[] bytes, int offset)
      throws InvalidKeyException
  {
    if (bytes == null || bytes.length - offset < 1) {
      throw new InvalidKeyException("No key type identifier");
    }

    if (offset == 0) {
      return new ECPublicKey(bytes);
    } else {
      int wantedLen = bytes.length - offset;
      byte[] wanted = new byte[wantedLen];
      System.arraycopy(bytes, offset, wanted, 0, wantedLen);
      return new ECPublicKey(wanted);
    }
  }

  public static ECPrivateKey decodePrivatePoint(byte[] bytes) {
    return new ECPrivateKey(bytes);
  }

  public static byte[] calculateAgreement(ECPublicKey publicKey, ECPrivateKey privateKey)
      throws InvalidKeyException
  {
    if (publicKey == null) {
      throw new InvalidKeyException("public value is null");
    }

    if (privateKey == null) {
      throw new InvalidKeyException("private value is null");
    }

    return privateKey.calculateAgreement(publicKey);
  }

  public static boolean verifySignature(ECPublicKey signingKey, byte[] message, byte[] signature)
      throws InvalidKeyException
  {
    if (signingKey == null || message == null || signature == null) {
      throw new InvalidKeyException("Values must not be null");
    }

    return signingKey.verifySignature(message, signature);
  }

  public static byte[] calculateSignature(ECPrivateKey signingKey, byte[] message)
      throws InvalidKeyException
  {
    if (signingKey == null || message == null) {
      throw new InvalidKeyException("Values must not be null");
    }

    return signingKey.calculateSignature(message);
  }

}
