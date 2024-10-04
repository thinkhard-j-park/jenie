db.board.insertMany([{
    "_id": "test-board-id",
    "name": "test-board",
    "parentId": "",
    "rootId": ""
}])

db.getCollection("article-header").createIndex(
    {
        boardId: 1,
        state: 1,
        _id: 1
    },
    {
        name: "boardId_state_id"
    }
);

db.getCollection("article-header").createIndex(
    {
        state: 1,
        _id: 1
    },
    {
        name: "state_id"
    }
);
