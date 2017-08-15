package machine.slicing.server;

import java.util.ArrayList;
import java.util.Arrays;

import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.StatusCode;
import org.opcfoundation.ua.builtintypes.UnsignedInteger;
import org.opcfoundation.ua.core.BrowseResult;
import org.opcfoundation.ua.core.ReferenceDescription;

public class ContinuationPoint {
	private UnsignedInteger requestedMaxReferencesPerNode;
	private ReferenceDescription[] referenceDescriptions;
	private NodeId authenticationToken;
	private byte[] currentContinuationPoint;
	public ContinuationPoint(UnsignedInteger requestedMaxReferencesPerNode,
							 ReferenceDescription[] referenceDescriptions,
							 NodeId authenticationToken, 
							 byte[] currentContinuationPoint) {
		this.requestedMaxReferencesPerNode = requestedMaxReferencesPerNode;
		this.referenceDescriptions = referenceDescriptions;
		this.authenticationToken = authenticationToken;
		this.currentContinuationPoint = currentContinuationPoint;
	}

	/**
	 * @return the authenticationToken
	 */
	public NodeId getAuthenticationToken() {
		return authenticationToken;
	}

	/**
	 * @return the currentContinuationPoint
	 */
	public byte[] getCurrentContinuationPoint() {
		return currentContinuationPoint;
	}

	/**
	 * @return those references that belong to next BrowseNext response
	 * @param continuationPointRequested
	 *            identify current continuation points
	 */
	public BrowseResult getNextReferencesDescriptions(byte[] continuationPointRequested) {
		// byte[] continuationPointRequested may be used to identify different continuation points
		ArrayList<ReferenceDescription> referenceDescriptionsToReturn = new ArrayList<ReferenceDescription>();
		ArrayList<ReferenceDescription> originalReferenceDescriptions = new ArrayList<ReferenceDescription>(
																				  Arrays.asList(referenceDescriptions));
		int length = Math.min(this.requestedMaxReferencesPerNode.intValue(), referenceDescriptions.length);
		// return only certain amount of references
		referenceDescriptionsToReturn.addAll(originalReferenceDescriptions.subList(0, length));
		// Remove these references from this ContinuationPoint
		originalReferenceDescriptions.subList(0, length).clear();
		referenceDescriptions = originalReferenceDescriptions.toArray(new ReferenceDescription[originalReferenceDescriptions
				                                  .size()]);
		// return referenceDescriptionsToReturn;
		if (referenceDescriptions.length > 0) {
			this.currentContinuationPoint = new byte[] { (byte) (continuationPointRequested[0] + (byte) 1) };
			return new BrowseResult(
					StatusCode.GOOD,
					currentContinuationPoint,
					referenceDescriptionsToReturn.toArray(new ReferenceDescription[referenceDescriptionsToReturn.size()]));
		}
		// if no references are left, then do not return continuationPoint
		// anymore
		return new BrowseResult(
				StatusCode.GOOD,
				null,
				referenceDescriptionsToReturn.toArray(new ReferenceDescription[referenceDescriptionsToReturn.size()]));
	}

	public ReferenceDescription[] getReferenceDescriptions() {
		return this.referenceDescriptions;
	}

	public UnsignedInteger getRequestedMaxReferencesPerNode() {
		return this.requestedMaxReferencesPerNode;
	}

	/**
	 * @param authenticationToken
	 *            the authenticationToken to set
	 */
	public void setAuthenticationToken(NodeId authenticationToken) {
		this.authenticationToken = authenticationToken;
	}

	/**
	 * @param currentContinuationPoint
	 *            the currentContinuationPoint to set
	 */
	public void setCurrentContinuationPoint(byte[] currentContinuationPoint) {
		this.currentContinuationPoint = currentContinuationPoint;
	}

	public void setReferenceDescriptions(ReferenceDescription[] referenceDescriptions) {
		this.referenceDescriptions = referenceDescriptions;
	}

	public void setRequestedMaxReferencesPerNode(UnsignedInteger requestedMaxReferencesPerNode) {
		this.requestedMaxReferencesPerNode = requestedMaxReferencesPerNode;
	}
}
