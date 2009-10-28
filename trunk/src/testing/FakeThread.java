package testing;

import java.io.Serializable;

import utility.Opcode;
import utility.Packet;
import utility.TransactionThread;

/**
 * An empty class that implements TransactionThread in order to avoid errors
 * during testing
 * @author bmsmolin
 *
 */
public class FakeThread implements TransactionThread {

	@Override
	public void send(Packet packet) {
		// TODO Auto-generated method stub

	}

	@Override
	public void send(Opcode opcode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void send(Opcode opcode, Serializable datum) {
		// TODO Auto-generated method stub

	}

}
