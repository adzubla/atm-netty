#!/bin/sh

umask 011

echo "Starting container..."
echo "$(java -version)"
echo "JAVA_OPTS: ${JAVA_OPTS}"

if [ -n "$STARTUP_WAIT_TIME" ] && [ "$STARTUP_WAIT_TIME" -gt 0 ]
then
  echo "Waiting ${STARTUP_WAIT_TIME} seconds before launch."
  sleep "$STARTUP_WAIT_TIME"
fi

sh -c 'java $JAVA_OPTS -cp app:app/lib/* com.example.responder.DummyResponderApplication'
