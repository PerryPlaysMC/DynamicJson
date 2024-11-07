package io.dynamicstudios.json.data.util.packet;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Creator: PerryPlaysMC
 * Created: 01/2022
 **/
public class PacketDataSerializer {

  private final ByteBuf data;

  public PacketDataSerializer(ByteBuf buf) {
    this.data = buf;
  }

  public ByteBuf writeByte(int i) {
    return this.data.writeByte(i);
  }

  public ByteBuf writeByte(byte i) {
    return this.data.writeByte(i);
  }

  public ByteBuf writeBytes(byte[] abyte) {
    return this.data.writeBytes(abyte);
  }

	public boolean readBoolean() {
		return this.data.readBoolean();
	}

	public PacketDataSerializer writeBoolean(boolean data) {
		this.data.writeBoolean(data);
		return this;
	}

  public PacketDataSerializer write(int i) {
    while((i & -128) != 0) {
      this.writeByte(i & 127 | 128);
      i >>>= 7;
    }
    this.writeByte(i);
    return this;
  }

  public PacketDataSerializer writeString(String s) {

    return this.writeStringMax(s, 32767);
  }

  public PacketDataSerializer writeUUID(UUID uuid) {
    this.writeLong(uuid.getMostSignificantBits());
    this.writeLong(uuid.getLeastSignificantBits());
    return this;
  }

	public UUID readUUID() {
		return new UUID(readLong(), readLong());
	}

  public PacketDataSerializer writeStringMax(String s, int i) {
    byte[] abyte = s.getBytes(StandardCharsets.UTF_8);
    if(abyte.length > i) {
      throw new EncoderException("String too big (was " + abyte.length + " bytes encoded, max " + i + ")");
    } else {
      this.write(abyte.length);
      this.writeBytes(abyte);
      return this;
    }
  }

  public void writeLong(Long value) {
    this.data.writeLong(value);
  }
	public long readLong() {
		return this.data.readLong();
	}

	public byte readByte() {
		return data.readByte();
	}

  public int readInt() {
    int i = 0;
    int j = 0;

    byte b0;
    do {
      b0 = data.readByte();
      i |= (b0 & 127) << j++ * 7;
      if(j > 5) {
        throw new RuntimeException("VarInt too big");
      }
    } while((b0 & 128) == 128);

    return i;
  }

  public ByteBuf readerIndex(int i) {
    return this.data.readerIndex(i);
  }


  public int readerIndex() {
    return this.data.readerIndex();
  }

  public String toString(int i) {
    int j = this.readInt();
    if(j > i * 4) {
      throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + i * 4 + ")");
    } else if(j < 0) {
      throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
    } else {
      String s = this.toString(this.readerIndex(), j, StandardCharsets.UTF_8);
      this.readerIndex(this.readerIndex() + j);
      if(s.length() > i) {
        throw new DecoderException("The received string length is longer than maximum allowed (" + j + " > " + i + ")");
      } else {
        return s;
      }
    }
  }

  public String toString(int i, int j, Charset charset) {
    return this.data.toString(i, j, charset);
  }


  public ByteBuf getData() {
    return data;
  }

}
