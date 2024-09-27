db.board.insertMany([{
    "_id": "test-board-id",
    "name": "test-board",
    "parentId": "",
    "rootId": ""
}])

db.getCollection("article-header").createIndex(
    {
        boardId: 1,
        _id: 1
    },
    {
        name: "boardId_id"
    }
);
