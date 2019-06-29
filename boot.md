# Joining the Ergo main network at the very beginning

In order to join the network at its very beginning and compete to mine the genesis block, 
you need to follow the steps described below. 
If you are not going to participate in Ergo mining and just want to run a full node, you need to follow only steps 3 and 5.

## Preparations

### 1. Build mining software 

Check the https://github.com/ergoplatform/ergo/wiki/Mining guide first.

Build a https://github.com/ergoplatform/Autolykos-GPU-miner miner according to its readme instruction (for Ubuntu) or using  Windows manual https://ergoplatform.org/en/blog/2019_05_07_mining.

Try to run it like `./auto.out config.json` with test config:
```
{
  "mnemonic": "noise peasant subway frozen illegal pretty oak agent train valid wash title burst column yard decide move coin gas asset pretty hire happy fuel",
  "node": "http://159.203.36.162:9052",
  "keepPrehash": true
}
```
and you if you see something like:
```
2019-06-27 14:44:58,626 INFO [main thread] Using 1 GPU devices
2019-06-27 14:44:58,626 INFO [main thread] Using configuration file mine162.json
2019-06-27 14:44:58,628 INFO [main thread] Block getting URL:
   http://159.203.36.162:9052/mining/candidate
2019-06-27 14:44:58,628 INFO [main thread] Solution posting URL:
   http://159.203.36.162:9052/mining/solution
2019-06-27 14:44:58,629 INFO [main thread] Generated public key:
      pk = 0x03 F5214F2F6D87C714 4A2E12F8D4C4E110 1C59FF7D9AEABC11 77C4CB50FA8497E7
2019-06-27 14:44:58,714 INFO [main thread] Got new block in main thread, block data: {
  "MSG" : "3637B40440681781F92EDC92B33E623330F05705446168952CBD3720BA922442",
  "B" : 3462030106355215694294694818737961234095984491840283822105474632310,
  "PK" : "03F5214F2F6D87C7144A2E12F8D4C4E1101C59FF7D9AEABC1177C4CB50FA8497E7"
}
2019-06-27 14:44:58,956 INFO [GPU 0 miner] GPU 0 allocating memory
2019-06-27 14:44:58,964 INFO [GPU 0 miner] Preparing unfinalized hashes on GPU 0
2019-06-27 14:45:13,443 INFO [GPU 0 miner] GPU 0 read new block data
2019-06-27 14:48:02,298 INFO [main thread] Average hashrates: GPU0 24.8537 MH/s Total 24.8537 MH/s 
```
you miner is built correctly.

### 2. Generate mnemonic phrase for mining 

???

Keep your mnemonic phrase in secret and write down your public key.

### 3. Configure and run your full node

Check https://github.com/ergoplatform/ergo/wiki/Set-up-a-full-node guide first.

Download the latest mainnet release from ???. 
Create a `settings.conf` config file with content:
```
ergo {
  node {
    mining = true
    miningPubKeyHex = "<your public key from step 2>"
  }
}

scorex {
  network {
    nodeName = "<some name to identify your node>"
  }
  restApi {
    # you may wish to configure an apiKeyHash to use your wallet. Check https://ergoplatform.org/en/blog/2019_06_04_wallet-documentation/ for wallet documentation.
    apiKeyHash = "1111"
  }
}
```

Run your node like `nohup java -jar -Xmx3G -Dlogback.stdout.level=DEBUG ergo-???.jar --mainnet -c settings.conf` and you if you see something like:
```
17:02:11.449 INFO  [main] o.e.settings.ErgoSettings$ - Running in mainnet network mode
17:02:12.262 INFO  [main] org.ergoplatform.ErgoApp - Entering coordinated network bootstrap procedure ..
17:02:12.933 INFO  [main] o.e.BootstrapController - Wrong response format, retrying in 10s
17:02:22.933 INFO  [main] o.e.BootstrapController - Wrong response format, retrying in 10s
```
you node is configured correctly. No more actions is required with the node.

### 4. Configure and start your miner

Go back to your mining hardware to configure and start your miner.
 
Create a `settings.json` config file with content:
```
{
  "mnemonic": "<mnemonic generated at step 2>",
  "node": "http://<ip address of your node>:9052",
  "keepPrehash": true
}
```

and run a miner like `./auto.settings config.json`. It should initialize and start requesting a node for the block candidate. 
```
2019-06-27 15:14:02,267 INFO [main thread] Using 1 GPU devices
2019-06-27 15:14:02,267 INFO [main thread] Using configuration file mainnet-conf.json
2019-06-27 15:14:02,269 INFO [main thread] Block getting URL:
   http://<ip address of your node>:9052/mining/candidate
2019-06-27 15:14:02,269 INFO [main thread] Solution posting URL:
   http://<ip address of your node>:9052/mining/solution
2019-06-27 15:14:02,269 INFO [main thread] Generated public key:
      pk = 0x02 F611D5F6AAB70C05 4A530C6420395B3C 4521642DC7125A93 49AAA2D9BB89D7AF
2019-06-27 15:14:02,407 ERROR [main thread] CURL: Couldn't connect to server
2019-06-27 15:14:03,345 ERROR [main thread] CURL: Couldn't connect to server
2019-06-27 15:14:04,280 ERROR [main thread] CURL: Couldn't connect to server

```

No more actions are required with the node.

### 5. Wait for the mainnet launch

After all this step you're ready to participate in Ergo mining and should just wait. Your miner is waiting for block candidate from your node, while your node is waiting for proof-of-no-premine that will be broadcasted by the developers' team to a main network launch schedule. No-premine proof will contain the headlines from the media (The Guardian, Vedomosti, Xinhua), as well as the latest block identifiers from Bitcoin and Ethereum. When the launch time will come, we'll distribute a no-premine proof and the mining will start.

To ensure that your node is mining, you may check your node log. It should stop to write logs from `BootstrapController` and write `Boot settings received. Starting the node`, so the logs file will looks like:
```
17:21:39.417 INFO  [main] o.e.BootstrapController - Wrong response format, retrying in 10s
17:21:49.437 INFO  [main] o.e.BootstrapController - Wrong response format, retrying in 10s
17:21:59.500 INFO  [main] org.ergoplatform.ErgoApp - Boot settings received. Starting the node ..
17:21:59.554 INFO  [ctor.default-dispatcher-3] s.c.n.NetworkController - Declared address: None
17:21:59.558 INFO  [ctor.default-dispatcher-3] s.c.n.NetworkController - Registering handlers for List((1,GetPeers message), (2,Peers message))
17:21:59.559 INFO  [ctor.default-dispatcher-3] s.c.n.NetworkController - Successfully bound to the port 9030
17:21:59.565 INFO  [ctor.default-dispatcher-4] o.e.n.state.ErgoState$ - Generating genesis UTXO state
17:21:59.568 INFO  [ctor.default-dispatcher-2] o.e.local.ErgoMiner - Trying to use key from wallet for mining
17:21:59.583 INFO  [ctor.default-dispatcher-3] s.c.u.NetworkTimeProvider - New offset adjusted: 2
17:21:59.585 WARN  [ctor.default-dispatcher-3] o.e.n.ErgoReadersHolder - Got GetReaders request in state (None,None,None,None)
17:21:59.598 INFO  [ctor.default-dispatcher-2] s.c.n.NetworkController - Registering handlers for List((55,Inv), (22,RequestModifier), (33,Modifier), (65,Sync))
17:21:59.729 INFO  [ctor.default-dispatcher-4] s.c.a.a.b.VersionedIODBAVLStorage - Update storage to version ByteArrayWrapper[6813BCA7232C6E156FB6229ECF165D157640A8576A5B320506E4C1B66011253402]: 14 elements to insert, 1 elements to remove
17:21:59.846 INFO  [ctor.default-dispatcher-4] o.e.n.state.ErgoState$ - Genesis UTXO state generated with hex digest 6813bca7232c6e156fb6229ecf165d157640a8576a5b320506e4c1b66011253402
```
At the same time your miner should write you something like:
```
2019-06-27 15:14:03,345 ERROR [main thread] CURL: Couldn't connect to server
2019-06-27 15:14:04,280 ERROR [main thread] CURL: Couldn't connect to server
2019-06-27 15:26:20,328 INFO [main thread] Got new block in main thread, block data: {
  "MSG" : "0777078E22BB64C771DE8A8D3B1E454847A91024D98AB86949091C8019FA7453",
  "B" : 964934076977634961863091541739065898773646368992290869855043026179318012,
  "PK" : "02F611D5F6AAB70C054A530C6420395B3C4521642DC7125A9349AAA2D9BB89D7AF"
}
2019-06-27 14:44:58,956 INFO [GPU 0 miner] GPU 0 allocating memory
2019-06-27 14:44:58,964 INFO [GPU 0 miner] Preparing unfinalized hashes on GPU 0
2019-06-27 14:45:13,443 INFO [GPU 0 miner] GPU 0 read new block data
2019-06-27 14:48:02,298 INFO [main thread] Average hashrates: GPU0 24.8537 MH/s Total 24.8537 MH/s 
2019-06-27 15:27:11,033 INFO [GPU 0 miner] GPU 0 found and trying to POST a solution:
```
