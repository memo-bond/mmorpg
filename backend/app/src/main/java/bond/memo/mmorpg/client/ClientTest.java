package bond.memo.mmorpg.client;

import bond.memo.mmorpg.models.PlayerActions;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.util.logging.Logger;

public class ClientTest {

    private static final Logger logger = Logger.getLogger(ClientTest.class.getName());

    public static void main(String[] args) throws Exception {

        PlayerActions.Join join = PlayerActions.Join.newBuilder()
                .setId(1)
                .build();
        PlayerActions.PlayerMessage message = PlayerActions.PlayerMessage.newBuilder()
                .setJoin(join)
                .build();

        sendMsg(message);
    }

    static void sendMsg(PlayerActions.PlayerMessage message) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast(new ProtobufEncoder());
                        }
                    });

            ChannelFuture f = b.connect("localhost", 6666).sync();
            Channel channel = f.channel();

            channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    logger.info("Message sent successfully");
                } else {
                    logger.warning("Failed to send message: " + future.cause().getMessage());
                }
                // Close the channel and event loop group after sending the message
                channel.close().addListener(ChannelFutureListener.CLOSE);
                group.shutdownGracefully();
            });

            // Wait until the connection is closed
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
