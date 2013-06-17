/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

import java.io.*;
import java.net.*;

public class ijClient {
    public static void main(String[] args) throws IOException {

        Socket ijSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

		String computername = InetAddress.getLocalHost().getHostName();

        try {
            ijSocket = new Socket(computername, 4444);
            out = new PrintWriter(ijSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(ijSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + computername);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + computername);
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromClient;

        while ((fromServer = in.readLine()) != null) {
            if (fromServer.equals("Bye.")){
                out.println(fromServer);
                System.out.println("client closed");
                break;
            }
		    
            fromClient = stdIn.readLine();  //stores message from std input
			if (fromClient != null) {
                System.out.println("Client: " + fromClient);
                out.println(fromClient);    //send message to server
			}
            System.out.println("Server: " + fromServer);

        }

        out.close();
        in.close();
        stdIn.close();
        ijSocket.close();
    }
}
