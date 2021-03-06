package at.ac.ait.archistar.bft.ozymandias.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.ait.archistar.bft.ozymandias.commands.client.ClientCommand;
import at.ac.ait.archistar.bft.ozymandias.commands.client.TransactionResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Listener for incoming replica messages. Currently only a transaction's result
 * is transmitted.
 * 
 * @author andy
 */
public class OzymandiasClientHandler extends SimpleChannelInboundHandler<ClientCommand> {

	private Logger logger = LoggerFactory.getLogger(OzymandiasClientHandler.class);

    
    private OzymandiasClient ozymandiasClient;
    
    public OzymandiasClientHandler(OzymandiasClient ozymandiasClient) {
    	this.ozymandiasClient = ozymandiasClient;
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Unexpected exception from downstream.", cause);
        ctx.close();
    }

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, ClientCommand msg) throws Exception {
		logger.debug("received: {}", msg);
		if (msg instanceof TransactionResult) {
			this.ozymandiasClient.positiveResultCountReached(msg.getClientId(), msg.getClientSequence(), (TransactionResult)msg);
		} else {
			logger.warn("unknown command: {}", msg);
		}
	}
}
