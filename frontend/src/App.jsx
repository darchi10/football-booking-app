import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import ProtectedRoute from "./components/ProtectedRoute";
import DashboardPage from "./pages/DashboardPage";
import BookingPage from './pages/BookingPage';
import Navbar from "./components/Navbar";
import MyReservationPage from "./pages/MyReservationsPage";
import AdminRoute from "./components/adminRoute";
import AdminFieldsPage from './pages/AdminFieldsPage';
import RegisterPage from "./pages/RegisterPage";
import ProtectedRouteSigned from "./components/ProtectedRouteSigned";


function App() {
  return ( 
    <Router>
      <Navbar /> 
      <Routes>
        <Route element={<ProtectedRouteSigned />}>
          <Route path="/login" element={<LoginPage/>} />
          <Route path="/register" element={<RegisterPage />} />
        </Route>

        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/book/:fieldId" element={<BookingPage />} />
          <Route path="/my-reservations" element={<MyReservationPage />} />
        </Route>
        <Route element={<AdminRoute />}> 
          <Route path="/admin/fields" element={<AdminFieldsPage />} />
        </Route>
      </Routes>
    </Router>
  )
}

export default App
