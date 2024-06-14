using System;
using Google.Protobuf;

public static class ByteUtils
{
    public static byte[] massageMsg(IMessage message)
    {
        byte[] data = message.ToByteArray();

        // Prepare the message with length prefix
        byte[] lengthPrefix = BitConverter.GetBytes(data.Length);
        if (BitConverter.IsLittleEndian)
        {
            Array.Reverse(lengthPrefix);
        }

        // Combine length prefix and data into one array
        byte[] messageWithPrefix = new byte[lengthPrefix.Length + data.Length];
        Buffer.BlockCopy(lengthPrefix, 0, messageWithPrefix, 0, lengthPrefix.Length);
        Buffer.BlockCopy(data, 0, messageWithPrefix, lengthPrefix.Length, data.Length);

        return messageWithPrefix;
    }
}
