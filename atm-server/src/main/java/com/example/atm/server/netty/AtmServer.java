package com.example.atm.server.netty;

import com.example.atm.netty.codec.atm.AtmDecoder;
import com.example.atm.netty.codec.atm.AtmEncoder;
import com.example.atm.netty.codec.crypto.CryptoDecoder;
import com.example.atm.netty.codec.crypto.CryptoEncoder;
import com.example.atm.netty.codec.header.HeaderDecoder;
import com.example.atm.netty.codec.header.HeaderEncoder;
import com.example.atm.netty.codec.length.LengthFrameDecoder;
import com.example.atm.netty.codec.length.LengthPrepender;
import com.example.atm.netty.codec.mac.MacDecoder;
import com.example.atm.server.AtmServerProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Servidor de comunicação com ATM.</p>
 * <p>Formato das mensagens recebidas:</p>
 * <p/>
 * <h1>Frame:</h1>
 * <pre>
 *     +-----------+----------------+---------------------+
 *     | tamanho   |   cabecalho    |      corpo          |
 *     | (2 bytes) |   (30 bytes)   |      (x bytes)      |
 *     +-----------+----------------+---------------------+
 * </pre>
 * <ul>
 *     <li>tamanho: tamanho da mensagem completa (2 + 30 + x)</li>
 *     <li>cabecalho: informações sobre mensagem</li>
 *     <li>corpo: mensagem encriptada</li>
 * </ul>
 * <p/>
 * <h1>Cabecalho:</h1>
 * <pre>
 *     +----------+----------+----------+----------+-----------+------------+------------+
 *     | versao   | formato  | servico  | tipo     | formatoId | id         | reservado  |
 *     | (1 byte) | (1 byte) | (1 byte) | (1 byte) | (1 byte)  | (12 bytes) | (13 bytes) |
 *     +----------+----------+----------+----------+-----------+------------+------------+
 * </pre>
 * <ul>
 *     <li>versao: 0x01</li>
 *     <li>formato: 0x01 - ISO8583, 0x02 - Texto (GFT)</li>
 *     <li>servico: 0x03 - GFT, 0x05 - Transacional (ATM)</li>
 *     <li>tipo: 0x01 - ping, 0x02 - pong, 0x03 - dados</li>
 *     <li>formatoId: 0x01</li>
 *     <li>id: string no formato ASCII:<br>
 *         <pre>['00000'][IdTerminal (7 bytes)]</pre></li>
 *     <li>reservado: preenchido com 0x00</li>
 * </ul>
 * <p/>
 * <h1>Corpo (Depois de decriptado):</h1>
 * <pre>
 *      +---------------------------+------------------+
 *      |        mensagem iso       |  mac (opcional)  |
 *      |        (y bytes)          |  (32 bytes)      |
 *      +---------------------------+------------------+
 * </pre>
 * <ul>
 *     <li>mensagem iso: mensagem no formato ISO 8583</li>
 *     <li>mac: código hash para a autenticação da mensagem (DES/ECB/PKCS5Padding).<br>
 *     O mac está presente se (!bit01 && bit64) || (bit01 && bit128)</li>
 * </ul>
 * <p/>
 * <h1>Mensagem iso:</h1>
 * <pre>
 *     +-------------+--------------+--------------------------+
 *     |  mti        |  bitmap      |    fields                |
 *     |  (4 bytes)  |  (16 bytes)  |    (y - 4 - 16 bytes)    |
 *     +-------------+--------------+--------------------------+
 * </pre>
 * <ul>
 *     <li>mti: Message Type Indicator, é um campo numérico de quatro digitos decimais</li>
 *     <li>bitmap: 16 caracteres hexadecimais no formato ASCII</li>
 *     <li>fields: campos especificados no bitmap</li>
 * </ul>
 * <p/>
 * <p><a href="https://en.wikipedia.org/wiki/ISO_8583">ISO-8583</a></p>
 */
public final class AtmServer {
    private static final Logger LOG = LoggerFactory.getLogger(AtmServer.class);

    private final ServerBootstrap bootstrap;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private final EventExecutorGroup cryptoGroup;
    private final EventExecutorGroup handlerGroup;

    public AtmServer(AtmServerProperties config, AtmServerListener listener) {
        bossGroup = TransportType.newEventLoopGroup(config.getBossThreads());
        workerGroup = TransportType.newEventLoopGroup(config.getWorkerThreads());

        cryptoGroup = new DefaultEventExecutorGroup(config.getCryptoThreads());
        handlerGroup = new DefaultEventExecutorGroup(config.getHandlerThreads());

        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(TransportType.getServerChannelClass())
                .option(ChannelOption.SO_BACKLOG, config.getSoBacklog())
                .childOption(ChannelOption.SO_KEEPALIVE, config.isSoKeepalive())
                .childOption(ChannelOption.TCP_NODELAY, config.isTcpNodelay())
                .handler(new LoggingHandler(LogLevel.INFO))
                .localAddress(config.getSocketPort())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new LengthFrameDecoder());
                        pipeline.addLast(cryptoGroup, new CryptoDecoder());
                        pipeline.addLast(new MacDecoder());
                        pipeline.addLast(new HeaderDecoder());
                        pipeline.addLast(new AtmDecoder());

                        pipeline.addLast(new LengthPrepender());
                        pipeline.addLast(cryptoGroup, new CryptoEncoder());
                        pipeline.addLast(new HeaderEncoder());
                        pipeline.addLast(new AtmEncoder());

                        pipeline.addLast(handlerGroup, new AtmServerHandler(listener));
                    }
                });
        bootstrap.validate();
    }

    public void start() throws InterruptedException {
        LOG.info("Starting");
        bootstrap.bind().sync().await();
    }

    public void shutdown() throws InterruptedException {
        LOG.info("Shutdown workerGroup");
        if (workerGroup != null) {
            workerGroup.shutdownGracefully().sync().await();
        }
        LOG.info("Shutdown bossGroup");
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().sync().await();
        }
        LOG.info("Shutdown cryptoGroup");
        if (cryptoGroup != null) {
            cryptoGroup.shutdownGracefully().sync().await();
        }
        LOG.info("Shutdown handlerGroup");
        if (handlerGroup != null) {
            handlerGroup.shutdownGracefully().sync().await();
        }
    }

}
