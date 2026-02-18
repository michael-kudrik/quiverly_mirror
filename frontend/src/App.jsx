import { useState, useEffect} from 'react'
import Navbar from './components/Navbar'
import BoardList from './components/BoardList'
import BoardForm from './components/BoardForm';
import useFetch from './hooks/useFetch';
function App() {
  
const [isFormOpen, setIsFormOpen] = useState(false);
const [boards, setBoards] = useState([]);

const {
    data,
    loading,
    error,
  } = useFetch("http://localhost:8080/api/v1/surfboard");
  //   http://localhost:8080/api/v1/surfboard
  
useEffect(() => {
    if (data) {
      setBoards(data);
    }
  }, [data]);

  return (
    <div className="bg-sand min-h-screen">
     <Navbar/> 
      <div >
     <BoardList boards={boards} loading={loading} error={error}/>
     </div>
     {/* 1. This is the button that triggers the form */}
      <button 
        onClick={() => setIsFormOpen(true)}
        className="bg-green-500 hover:bg-green-600 text-white font-bold py-3 px-8 rounded-full shadow-lg transition-all transform hover:scale-105"
      >
        + Add New Board
      </button>

      {/* 2. Pass the state and the closer function to the component */}
      <BoardForm 
        isOpen={isFormOpen} 
        onClose={() => setIsFormOpen(false)}
        onBoardCreated={(newBoard) =>
          setBoards(prev => [...prev, newBoard])
        } 
      />
    </div>
  )
}

export default App
