/**
 * <code>
 * Print a string at info level.
 * </code>
 * 
 * @param pMessage
 *            The message to print out.
 */
function print(pMessage) {
	emitter.print(pMessage);
}

/**
 * <code>
 * Print the currently contexts root element and all children at info level.
 * </code>
 */
function printDocument() {
	emitter.printDocument();
}

/**
 * <code>
 * Exclude a specific value from ever being emitted.  This has a global affect and 
 * should be used with care.
 * </code>
 * 
 * @param pValue
 *            The value to exclude.
 */
function addExcludeValue(pValue) {
	emitter.addExcludeValue(pValue);
}