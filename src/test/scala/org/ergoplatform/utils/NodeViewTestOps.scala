package org.ergoplatform.utils

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import org.ergoplatform.modifiers.history.Header
import org.ergoplatform.modifiers.{ErgoFullBlock, ErgoPersistentModifier}
import org.ergoplatform.nodeView.history.ErgoHistory
import org.ergoplatform.nodeView.mempool.ErgoMemPool
import org.ergoplatform.nodeView.state.{ErgoState, StateType}
import org.ergoplatform.nodeView.wallet.ErgoWallet
import org.ergoplatform.settings.Algos
import scorex.core.ModifierId
import scorex.core.NodeViewHolder.CurrentView
import scorex.core.NodeViewHolder.ReceivableMessages.{GetDataFromCurrentView, LocallyGeneratedModifier}
import scorex.util.ScorexLogging

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.reflect.ClassTag

trait NodeViewBaseOps extends ScorexLogging {

  implicit val timeout: Timeout = Timeout(5.seconds)
  val awaitDuration: FiniteDuration = timeout.duration + 1.second

  type Ctx = NodeViewTestContext
  type CurView = CurrentView[ErgoHistory, ErgoState[_], ErgoWallet, ErgoMemPool]

  def getCurrentView(implicit ctx: Ctx): CurView = {
    val request = GetDataFromCurrentView[ErgoHistory, ErgoState[_], ErgoWallet, ErgoMemPool, CurView](view => view)
    Await.result((nodeViewHolderRef ? request).mapTo[CurView], awaitDuration)
  }

  def getHistory(implicit ctx: Ctx): ErgoHistory = getCurrentView.history
  def getCurrentState(implicit ctx: Ctx): ErgoState[_] = getCurrentView.state

  def verifyTransactions(implicit ctx: Ctx): Boolean = ctx.settings.nodeSettings.verifyTransactions
  def stateType(implicit ctx: Ctx): StateType = ctx.settings.nodeSettings.stateType

  def applyBlock(fullBlock: ErgoFullBlock)(implicit ctx: Ctx): ErgoFullBlock = {
    nodeViewHolderRef ! LocallyGeneratedModifier(fullBlock.header)
    applyPayload(fullBlock)
    fullBlock
  }

  def applyPayload(fullBlock: ErgoFullBlock)(implicit ctx: Ctx): Unit = {
    if (verifyTransactions) {
      nodeViewHolderRef ! LocallyGeneratedModifier(fullBlock.blockTransactions)
      nodeViewHolderRef ! LocallyGeneratedModifier(fullBlock.adProofs.get)
      nodeViewHolderRef ! LocallyGeneratedModifier(fullBlock.extension)
    }
  }

  @inline private def nodeViewHolderRef(implicit ctx: Ctx): ActorRef = ctx.nodeViewHolderRef
  @inline def send(msg: Any)(implicit ctx: Ctx): Unit = ctx.testProbe.send(nodeViewHolderRef, msg)
  @inline def defaultTimeout(implicit ctx: Ctx): FiniteDuration = ctx.testProbe.remainingOrDefault
  @inline def expectMsg[T](obj: T)(implicit ctx: Ctx): T = ctx.testProbe.expectMsg(obj)
  @inline def expectMsgType[T](implicit ctx: Ctx, t: ClassTag[T]): T = ctx.testProbe.expectMsgType
  @inline def expectNoMsg()(implicit ctx: Ctx): Unit = ctx.testProbe.expectNoMessage(defaultTimeout)
  @inline def subscribeEvents(eventType: Class[_])(implicit ctx: Ctx): Boolean = {
    ctx.actorSystem.eventStream.subscribe(ctx.testProbe.ref, eventType)
  }
}

trait NodeViewTestOps extends NodeViewBaseOps {

  def getBestHeaderOpt(implicit ctx: Ctx): Option[Header] = getHistory.bestHeaderOpt
  def getPoolSize(implicit ctx: Ctx): Int = getCurrentView.pool.size
  def getRootHash(implicit ctx: Ctx): String = Algos.encode(getCurrentState.rootHash)
  def getBestFullBlockOpt(implicit ctx: Ctx):  Option[ErgoFullBlock] = getHistory.bestFullBlockOpt
  def getBestFullBlockEncodedId(implicit ctx: Ctx): Option[String] = getBestFullBlockOpt.map(_.header.encodedId)
  def getOpenSurfaces(implicit ctx: Ctx): Seq[ModifierId] = getHistory.openSurfaceIds()
  def getHistoryHeight(implicit ctx: Ctx): Int = getHistory.headersHeight
  def getModifierById(id: ModifierId)(implicit ctx: Ctx): Option[ErgoPersistentModifier] = getHistory.modifierById(id)
  def getHeightOf(id: ModifierId)(implicit ctx: Ctx): Option[Int] = getHistory.heightOf(id)
  def getLastHeadersLength(count: Int)(implicit ctx: Ctx): Int = getHistory.lastHeaders(count).size

  def getAfterGenesisStateDigest(implicit ctx: Ctx): Array[Byte] =
    ctx.settings.chainSettings.monetary.afterGenesisStateDigest
}

