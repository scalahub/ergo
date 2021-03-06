package org.ergoplatform.nodeView.history.storage

import org.ergoplatform.modifiers.history.HistoryModifierSerializer
import org.ergoplatform.utils.ErgoPropertyTest

class ObjectsStoreSpecification extends ErgoPropertyTest {

  val folder: String = createTempDir.getAbsolutePath
  val objectsStore = new FilesObjectsStore(folder)

  property("FilesObjectsStore: put, get, delete") {
    forAll(defaultHeaderGen) { header =>
      objectsStore.get(header.id) shouldBe None
      objectsStore.put(header)
      HistoryModifierSerializer.parseBytes(objectsStore.get(header.id).get) shouldBe header
      objectsStore.delete(header.id)
      objectsStore.get(header.id) shouldBe None
    }
  }

  property("Bulk objects insertion without losses") {
    val chain = genChain(2000)
    chain.foreach { b =>
      b.blockSections.foreach(objectsStore.put(_) shouldBe 'success)
    }
    chain.foreach { b =>
      b.blockSections.foreach(s => objectsStore.get(s.id).isDefined shouldBe true)
    }
  }

}
